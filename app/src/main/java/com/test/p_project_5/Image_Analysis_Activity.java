package com.test.p_project_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Image_Analysis_Activity extends AppCompatActivity {
    private ImageView iv_analysis_default, iv_analysis_now;
    private TextView tv_img_analysis, tv_simil;
    // 파이어베이스
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;

    private Button go_expression_analysis, go_expression_analysis2;
    private String uid, now_user, reason;
    private float simil = (float) 0.0;
    private Bitmap bitmap;
    // 로딩
    Analysis_Loading_Activity customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_analysis);

        iv_analysis_default = findViewById(R.id.iv_analysis_default);
        iv_analysis_now = findViewById(R.id.iv_analysis_now);
        tv_img_analysis = findViewById(R.id.tv_img_analysis);
        tv_simil = findViewById(R.id.tv_simil);

        customProgressDialog = new Analysis_Loading_Activity(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Bundle bundle = getIntent().getExtras();
        uid = bundle.getString("uid");
        now_user = bundle.getString("현재사용자");
        simil = bundle.getFloat("유사도");


        tv_simil.setText("유사도 : "+simil+"%");
        tv_img_analysis.setText("비교 결과 : "+now_user);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        iv_analysis_now.setImageBitmap(bitmap);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기

        getFireBaseProfileImage(uid);

        go_expression_analysis = findViewById(R.id.go_expression_analysis);
        go_expression_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(now_user.equals("차주")){
                    customProgressDialog.show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reason = "표정 분석 불통과";
                            customProgressDialog.dismiss();
                            Toast.makeText(Image_Analysis_Activity.this, "표정분석 통과", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Image_Analysis_Activity.this, Typing_Test_Activity.class);
                            intent.putExtra("uid", uid); // 사용자 고유 uid
                            intent.putExtra("현재사용자",now_user);
                            intent.putExtra("이유",reason);
                            startActivity(intent);
                        }
                    }, 2500);

                }
                else{
                    customProgressDialog.show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            reason = "표정 분석 불통과";
                            customProgressDialog.dismiss();
                            Toast.makeText(Image_Analysis_Activity.this, "표정분석 통과", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Image_Analysis_Activity.this, Typing_Test_Activity.class);
                            intent.putExtra("uid", uid); // 사용자 고유 uid
                            intent.putExtra("현재사용자",now_user);
                            intent.putExtra("이유",reason);
                            startActivity(intent);
                        }
                    }, 2500);
                }


            }
        });
//        go_expression_analysis2 = findViewById(R.id.go_expression_analysis2);
//        go_expression_analysis2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                now_user = "다른사람";
//                Intent intent = new Intent(Image_Analysis_Activity.this, Expression_Analysis_Activity.class);
//                intent.putExtra("uid", uid); // 사용자 고유 uid
//                intent.putExtra("현재사용자",now_user);
//                startActivity(intent);
//            }
//        });

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