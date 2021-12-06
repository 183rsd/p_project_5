package com.test.p_project_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;

public class Image_Analysis_Activity extends AppCompatActivity {
    private ImageView iv_analysis_default, iv_analysis_now;
    // 파이어베이스
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;

    private String uid;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analysis);

        iv_analysis_default = findViewById(R.id.iv_analysis_default);
        iv_analysis_now = findViewById(R.id.iv_analysis_now);

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        iv_analysis_now.setImageBitmap(bitmap);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기

        getFireBaseProfileImage(uid);

    }

    // 파이어베이스 스토리지에서 이미지 가져오기
    private void getFireBaseProfileImage(String uid){
        File file = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img"); // 이미지를 저장할 수 있는 디렉토리
        // 구분할 수 있게 /toolbar_images폴더에 넣어줌
        // 이 파일안에 저 디렉토리가 있는지 확인
        if(!file.isDirectory()){ // 디렉토리가 없으면
            file.mkdir(); // 디렉토리 만듦
        }
        downloadImg(uid);
    }

    // 이미지 다운로드해서 가져오기
    private void downloadImg(String uid){
        FirebaseStorage storage = FirebaseStorage.getInstance(); // 스토리지 인스턴스 생성
        StorageReference storageReference = storage.getReference(); // 스토리지 참조
        storageReference.child("profile_img/"+"profile"+uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("성공", String.valueOf(uri));
                Glide.with(getApplicationContext()).load(uri).into(iv_analysis_default);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }
}