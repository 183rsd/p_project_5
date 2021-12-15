package com.test.p_project_5;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import org.apache.commons.codec.binary.Base64;

public class Alcohol_Detected_Activity extends AppCompatActivity {

    private String uid, now_user;
    private ImageView iv_camera, iv_camera_img;
    private Button btn_image_analysis;
    Float simil = null;
    // 파이어베이스
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;

    // 카메라 이미지
    private File tempFile = null;
    Uri photoUri;
    private static final int PICK_FROM_CAMERA = 2;

    Handler handler = new Handler();
    int z = 0;


    // 로딩
    Analysis_Loading_Activity customProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 landscape(가로) 화면으로 고정
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_alcohol_detected);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");


        StartTimer();


        customProgressDialog = new Analysis_Loading_Activity(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기



        iv_camera = findViewById(R.id.iv_camera);
        iv_camera_img = findViewById(R.id.iv_camera_img);


        getFireBaseProfileImage(uid);

        btn_image_analysis = findViewById(R.id.btn_image_analysis);
        btn_image_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customProgressDialog.show();

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Bitmap bitmap = ((BitmapDrawable)iv_camera_img.getDrawable()).getBitmap();

                String s = saveBitmapToJpg(bitmap, uid); // 파일경로


                float scale = (float) (1024/(float)bitmap.getWidth());
                int image_w = (int) (bitmap.getWidth() * scale);
                int image_h = (int) (bitmap.getHeight() * scale);
                Bitmap resize = Bitmap.createScaledBitmap(bitmap, image_w, image_h, true);
                resize.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();


                Handler mHandler = new Handler();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        ImageAsyncTask_compare task = new ImageAsyncTask_compare();
//                        File f = new File(s);


                        byte[] bt = new byte[(int) tempFile.length()];
                        FileInputStream fis = null;
                        String strBase64 = "";
                        String id = "owner";
                        try {
                            fis = new FileInputStream(tempFile);
                            fis.read(bt);
                            strBase64 = new String(Base64.encodeBase64(bt));
                        } catch (Exception e) {
                            try {
                                throw e;
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        } finally {
                            try {
                                if (fis != null) {
                                    fis.close();
                                }
                            } catch (IOException e) {

                            } catch (Exception e) {

                            }

                        }
                        //솔트룩스 API 사용
                        try {
                            simil=task.execute(strBase64,id).get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
//                        // 유사도 수치
//                        Toast.makeText(getApplicationContext(), "" +simil , Toast.LENGTH_SHORT).show();

                        if(simil >= 50.0){
                            customProgressDialog.dismiss();
                            now_user = "차주";
                            Intent intent = new Intent(Alcohol_Detected_Activity.this, Image_Analysis_Activity.class);
                            intent.putExtra("uid", uid); // 사용자 고유 uid
                            intent.putExtra("image", byteArray);
                            intent.putExtra("현재사용자", now_user);
                            intent.putExtra("유사도",simil);
                            startActivity(intent);
                        }
                        else {
                            customProgressDialog.dismiss();
                            now_user = "차주가 아님";
                            Intent intent = new Intent(Alcohol_Detected_Activity.this, Image_Analysis_Activity.class);
                            intent.putExtra("uid", uid); // 사용자 고유 uid
                            intent.putExtra("image", byteArray);
                            intent.putExtra("현재사용자", now_user);
                            intent.putExtra("유사도",simil);
                            startActivity(intent);
                        }


                    }
                }, 1000);


            }
        });


    }

    //      카메라 캡쳐 함수
    private void capture() { // takePhoto

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "이미지 처리 오류. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                // 카메라 함수에서 photoUri는 file://형태의 uri 이 아니라, content://형태의 uri 임.
                // 다른 앱 간에 파일 공유 시 file:// 가 아닌 content:// uri를 사용해야 함. (보안 강화)
                photoUri = FileProvider.getUriForFile(this,"com.test.p_project_5.provider",tempFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);

            } else{
                photoUri = Uri.fromFile(tempFile);
//                uri_camera = FileProvider.getUriForFile(this,"com.test.p_project_5.provider",tempFile);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(intent, PICK_FROM_CAMERA);
            }
        }

    }

    // ImageFile의 경로를 가져올 메서드 선언
    private File createImageFile() throws IOException {
        // 파일이름을 세팅 및 저장경로 세팅
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        return image;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("TAG", tempFile.getAbsolutePath() + " 삭제 성공");
                        tempFile = null;
                    }
                }
            }
            return;
        }
        if (requestCode == PICK_FROM_CAMERA) {
            // 카메라를 통해 가져온 사진(크롭된사진) uri는 getRealPathFromUri함수를 통해 절대경로 추출.
            Glide.with(this).load(photoUri).into(iv_camera_img);
            iv_camera.setVisibility(View.INVISIBLE);
            iv_camera_img.setVisibility(View.VISIBLE);


        }
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
//                Glide.with(getApplicationContext()).load(uri).into(iv_default_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    private void StartTimer(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (z = 0; z <= 3; z++) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(String.valueOf(z).equals("1")){
                                iv_camera.setImageResource(R.drawable.two);
                            }
                            if(String.valueOf(z).equals("2")){
                                iv_camera.setImageResource(R.drawable.one);
                            }
                            if(String.valueOf(z).equals("3")){
                                capture();
                                iv_camera.setVisibility(View.INVISIBLE);
                                iv_camera_img.setVisibility(View.VISIBLE);

                            }
                        }
                    });
                    try {
                        Thread.sleep(1000); //딜레이 타임 조절
                    }catch (
                            InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
        }).start();
    }

    public String saveBitmapToJpg(Bitmap bitmap, String name){
        File storage = getCacheDir();
        String fileName = name + ".jpg";
        File imgFile = new File(storage, fileName);
        try{
            imgFile.createNewFile();
            FileOutputStream out = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, out);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return getCacheDir() + "/" + fileName;
    }


}