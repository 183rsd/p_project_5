package com.test.p_project_5;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView tv_call1, tv_call2; // 연락망 텍스트뷰
    private ImageView iv_update_photo, iv_user_img, iv_btn_update_call;
    Dialog call_update_dialog, photo_update_dialog; // 연락처, 이미지 변경 다이얼로그
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private Button btn_update_photo; // 이미지 변경 버튼

    private String uid, nickname, photoUrl;

    // 이미지
    private Context mContext = this;
    File file;
    String filePath;
    Uri photoUri, uri_camera;
    private final Boolean isPermisson = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile = null;
    private Boolean isCamera = false; // 이미지를 리사이징하는 단계에서 카메라에서 온 화면인지, 앨범에서 온 화면인지 구분
    private ActivityResultLauncher<Intent> resultLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 landscape(가로) 화면으로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_home);
        tedPermission();

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        photoUrl = intent.getStringExtra("photoUrl");
        uid = intent.getStringExtra("uid");


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기


        tv_call1 = findViewById(R.id.tv_call1);
        tv_call2 = findViewById(R.id.tv_call2);
        iv_btn_update_call = findViewById(R.id.iv_btn_update_call);

        btn_update_photo = findViewById(R.id.btn_update_photo);
        iv_user_img = findViewById(R.id.iv_user_img);



        // 연락처 수정 다이얼로그
        call_update_dialog = new Dialog(this);
        call_update_dialog.setContentView(R.layout.call_update_dialog);
        iv_btn_update_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_update();
            }
        });

        // 이미지 수정 다이얼로그
        photo_update_dialog = new Dialog(this);
        photo_update_dialog.setContentView(R.layout.photo_update_dialog);
        btn_update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { photo_update(); }
        });



        readUser(uid); // 순서 주의 (유저 정보 가져온 다음에 호출되어야 함)
        getFireBaseProfileImage(uid);

    }

    private void tedPermission() { // 테드퍼미션
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // 권한 요청 성공

            }

            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // 권한 요청 실패
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    // 화면 터치 시 키보드 내려감
    public boolean dispatchTouchEvent(MotionEvent ev){
        View focusView = getCurrentFocus();
        if(focusView != null){
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if(!rect.contains(x,y)){
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    // 파이어베이스 데이터 저장
    private void writeNewUser(String userId, String call1, String call2){
        UserData userData = new UserData(call1, call2);
        mDatabase.child("users").child(userId).setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(HomeActivity.this, "저장완료", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(HomeActivity.this, "저장실패", Toast.LENGTH_SHORT);
                    }
                });
    }

    // 파이어베이스 데이터 조회
    private void readUser(String userId){
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(UserData.class)!=null){
                    UserData post = snapshot.getValue(UserData.class);
                    tv_call1.setText(post.getCall1());
                    tv_call2.setText(post.getCall2());


                } else{
                    tv_call1.setText("설정안함");
                    tv_call2.setText("설정안함");

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    // 연락망 변경 함수
    private void call_update(){
        call_update_dialog.show();
        EditText et_callnum1 = call_update_dialog.findViewById(R.id.et_callnum1);
        EditText et_callnum2 = call_update_dialog.findViewById(R.id.et_callnum2);
        Button update_yes = call_update_dialog.findViewById(R.id.update_yes);
        Button update_no = call_update_dialog.findViewById(R.id.update_no);

        update_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_callnum1.getText().toString() == null || et_callnum2.getText().toString() == null){
                    Toast.makeText(HomeActivity.this, "올바르게 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else{
                    String call1 = et_callnum1.getText().toString();
                    String call2 = et_callnum2.getText().toString();

                    HashMap result = new HashMap<>();
                    result.put("call1", call1);
                    result.put("call2", call2);
                    writeNewUser(uid, call1, call2);
                    call_update_dialog.dismiss();
                    readUser(uid);
                    et_callnum1.setText(null);
                    et_callnum2.setText(null);
                }
            }
        });

        update_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_update_dialog.dismiss();
            }
        });
    }

    // 이미지 변경 함수
    private void photo_update(){
        photo_update_dialog.show();
        iv_update_photo = photo_update_dialog.findViewById(R.id.update_photo);
        ImageView user_img = (ImageView) findViewById(R.id.iv_user_img);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) user_img.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        iv_update_photo.setImageBitmap(bitmap);
        Button update_yes = photo_update_dialog.findViewById(R.id.photo_update_yes);
        Button update_no = photo_update_dialog.findViewById(R.id.photo_update_no);

        iv_update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialogRadio();
            }
        });

        update_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_Photo();
//                getFireBaseProfileImage(uid);
                photo_update_dialog.dismiss();
                Toast.makeText(getApplicationContext(), "완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
                intent.putExtra("nickname", nickname); // 닉네임
                intent.putExtra("photoUrl", photoUrl); // 프로필사진
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        update_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo_update_dialog.dismiss();
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


    // 갤러리 사진 로드 함수
    public void gallery() { // goToAlbum
        isCamera = true;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
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

        if (requestCode == PICK_FROM_ALBUM) {

            try{ // 갤러리에서 가져온 사진(크롭x) uri는 UriToPath함수를 통해 filePath 추출 (상대경로)
                photoUri = data.getData();    // 다른 앱 간에 파일 공유 시 file:// 가 아닌 content:// uri를 사용해야 함. (보안 강화)
                Glide.with(this).load(photoUri).into(iv_update_photo);
                filePath = UriToPath(mContext,photoUri);

            }catch (Exception e){

            }

        } else if (requestCode == PICK_FROM_CAMERA){
            // 카메라를 통해 가져온 사진(크롭된사진) uri는 getRealPathFromUri함수를 통해 절대경로 추출.
            Glide.with(this).load(photoUri).into(iv_update_photo);
//            filePath = getRealPathFromURI(uri_camera);

        }
    }

    // 이미지를 어느 방법으로 가져올지 정하는 다이얼로그
    private void photoDialogRadio(){
        final CharSequence[] PhotoModels = {"카메라로 촬영", "갤러리에서 선택"};
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(this);

        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("사진 선택");
        alt_bld.setItems(R.array.select_photo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Toast.makeText(ProfileActivity.this, PhotoModels[item] + "가 선택되었습니다.", Toast.LENGTH_SHORT).show();
                if (item == 0) { // 카메라
                    capture();
                    alt_bld.create().dismiss();


                } else if (item == 1) { // 갤러리
                    gallery();
                    alt_bld.create().dismiss();
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    // 사진 uri 값을 통해 상대경로 추출
    public static String UriToPath(Context mContext,Uri photoUri){
        Cursor cursor = null;
        String[] proj = {MediaStore.Images.Media.DATA};

        assert photoUri != null;
        cursor = mContext.getContentResolver().query(photoUri, proj, null, null, null);

        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        @SuppressLint("Range") String path = cursor.getString(column_index);
        Uri uri1 = Uri.fromFile(new File(path));

        cursor.close();
        return path;
    }

    // content uri를 절대경로로 변환
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }
        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try { int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex); }
        } finally {
            cursor.close();
        }
        return null;
    }

    // 파이어베이스로 이미지 저장 및 기존 이미지는 삭제
    private void update_Photo(){
        // storage
        FirebaseStorage storage = FirebaseStorage.getInstance(); // 스토리지 인스턴스 생성
        StorageReference storageReference = storage.getReference(); // 스토리지 참조
        // 파일명 만들기
        String filename = "profile" + uid + ".jpg";
        Uri file = photoUri;
        Log.d("uri", String.valueOf(file));
        StorageReference riversRef = storageReference.child("profile_img/"+filename);
        UploadTask uploadTask = riversRef.putFile(file);


        // 기존 이미지 삭제
        StorageReference desertRef = storageReference.child("profile_img/"+"profile"+ uid + ".jpg"); // 삭제할 이미지명
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            }
        });

        // 새로운 이미지 저장
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            }
        });

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
                Glide.with(getApplicationContext()).load(uri).into(iv_user_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }





}