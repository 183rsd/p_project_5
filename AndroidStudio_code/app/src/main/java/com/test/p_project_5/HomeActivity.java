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

import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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
import java.util.concurrent.ExecutionException;

public class HomeActivity extends AppCompatActivity {

    private TextView tv_call1, tv_call2; // ????????? ????????????
    private ImageView iv_update_photo, iv_user_img, iv_btn_update_call;
    Dialog call_update_dialog, photo_update_dialog; // ?????????, ????????? ?????? ???????????????
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private Button btn_update_photo, btn_update_call; // ?????????, ????????? ?????? ??????
    Float simil = null;
    private String uid, nickname, photoUrl;

    // ?????????
    private Context mContext = this;
    File file, file1;
    String filePath;
    Uri photoUri, uri_camera;
    private final Boolean isPermisson = true;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int PICK_FROM_CAMERA = 2;
    private File tempFile = null;
    private Boolean isCamera = false; // ???????????? ?????????????????? ???????????? ??????????????? ??? ????????????, ???????????? ??? ???????????? ??????
    private ActivityResultLauncher<Intent> resultLauncher;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tedPermission();

        Intent intent = getIntent();
        nickname = intent.getStringExtra("nickname");
        photoUrl = intent.getStringExtra("photoUrl");
        uid = intent.getStringExtra("uid");


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // ???????????? ????????? ?????? ????????????
        mAuth = FirebaseAuth.getInstance(); // ?????? ?????? ?????? ????????????
        mDatabase = FirebaseDatabase.getInstance().getReference(); // ?????????????????? realtim database?????? ?????? ????????????


        tv_call1 = findViewById(R.id.tv_call1);
        tv_call2 = findViewById(R.id.tv_call2);
        btn_update_call = findViewById(R.id.btn_update_call);

        btn_update_photo = findViewById(R.id.btn_update_photo);
        iv_user_img = findViewById(R.id.iv_user_img);



        // ????????? ?????? ???????????????
        call_update_dialog = new Dialog(this);
        call_update_dialog.setContentView(R.layout.call_update_dialog);
        btn_update_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                call_update();
            }
        });

        // ????????? ?????? ???????????????
        photo_update_dialog = new Dialog(this);
        photo_update_dialog.setContentView(R.layout.photo_update_dialog);
        btn_update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { photo_update(); }
        });



        readUser(uid); // ?????? ?????? (?????? ?????? ????????? ????????? ??????????????? ???)
        getFireBaseProfileImage(uid);

    }

    private void tedPermission() { // ???????????????
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                // ?????? ?????? ??????

            }

            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                // ?????? ?????? ??????
            }
        };

        TedPermission.with(this)
                .setPermissionListener(permissionListener)
                .setRationaleMessage(getResources().getString(R.string.permission_2))
                .setDeniedMessage(getResources().getString(R.string.permission_1))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();
    }


    // ?????? ?????? ??? ????????? ?????????
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

    // ?????????????????? ????????? ??????
    private void writeNewUser(String userId, String call1, String call2){
        UserData userData = new UserData(call1, call2);
        mDatabase.child("users").child(userId).setValue(userData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(HomeActivity.this, "????????????", Toast.LENGTH_SHORT);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(HomeActivity.this, "????????????", Toast.LENGTH_SHORT);
                    }
                });
    }

    // ?????????????????? ????????? ??????
    private void readUser(String userId){
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(UserData.class)!=null){
                    UserData post = snapshot.getValue(UserData.class);
                    tv_call1.setText(post.getCall1());
                    tv_call2.setText(post.getCall2());


                } else{
                    tv_call1.setText("????????????");
                    tv_call2.setText("????????????");

                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    // ????????? ?????? ??????
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
                    Toast.makeText(HomeActivity.this, "???????????? ??????????????????.", Toast.LENGTH_SHORT).show();
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

    // ????????? ?????? ??????
    private void photo_update(){
        photo_update_dialog.show();
        iv_update_photo = photo_update_dialog.findViewById(R.id.update_photo);
        ImageView user_img = (ImageView) findViewById(R.id.iv_user_img);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) user_img.getDrawable();
//        Bitmap bitmap = bitmapDrawable.getBitmap();
//        iv_update_photo.setImageBitmap(bitmap);
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
                Glide.with(getApplicationContext()).load(filePath).into(iv_user_img);

                image_soltlux();


                Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(HomeActivity.this, MenuActivity.class);
//                intent.putExtra("nickname", nickname); // ?????????
//                intent.putExtra("photoUrl", photoUrl); // ???????????????
//                intent.putExtra("uid", uid); // ????????? ?????? uid
//                startActivity(intent);
            }
        });

        update_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photo_update_dialog.dismiss();
            }
        });


    }


    //      ????????? ?????? ??????
    private void capture() { // takePhoto

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            tempFile = createImageFile();
        } catch (IOException e) {
            Toast.makeText(this, "????????? ?????? ??????. ?????? ??????????????????.", Toast.LENGTH_SHORT).show();
//            getActivity().finish();
            e.printStackTrace();
        }
        if (tempFile != null) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                // ????????? ???????????? photoUri??? file://????????? uri ??? ?????????, content://????????? uri ???.
                // ?????? ??? ?????? ?????? ?????? ??? file:// ??? ?????? content:// uri??? ???????????? ???. (?????? ??????)
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

    // ImageFile??? ????????? ????????? ????????? ??????
    private File createImageFile() throws IOException {
        // ??????????????? ?????? ??? ???????????? ??????
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile( imageFileName, ".jpg", storageDir );
        return image;
    }


    // ????????? ?????? ?????? ??????
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

            Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show();

            if(tempFile != null) {
                if (tempFile.exists()) {
                    if (tempFile.delete()) {
                        Log.e("TAG", tempFile.getAbsolutePath() + " ?????? ??????");
                        tempFile = null;
                    }
                }
            }

            return;
        }

        if (requestCode == PICK_FROM_ALBUM) {

            try{ // ??????????????? ????????? ??????(??????x) uri??? UriToPath????????? ?????? filePath ?????? (????????????)
                photoUri = data.getData();    // ?????? ??? ?????? ?????? ?????? ??? file:// ??? ?????? content:// uri??? ???????????? ???. (?????? ??????)
                Glide.with(this).load(photoUri).into(iv_update_photo);
                filePath = UriToPath(mContext,photoUri);

                file1 = new File(String.valueOf(filePath));

            }catch (Exception e){

            }

        } else if (requestCode == PICK_FROM_CAMERA){
            // ???????????? ?????? ????????? ??????(???????????????) uri??? getRealPathFromUri????????? ?????? ???????????? ??????.
            Glide.with(this).load(photoUri).into(iv_update_photo);
//            filePath = getRealPathFromURI(uri_camera);

        }
    }

    // ???????????? ?????? ???????????? ???????????? ????????? ???????????????
    private void photoDialogRadio(){
        final CharSequence[] PhotoModels = {"???????????? ??????", "??????????????? ??????"};
        android.app.AlertDialog.Builder alt_bld = new android.app.AlertDialog.Builder(this);

        //alt_bld.setIcon(R.drawable.icon);
        alt_bld.setTitle("?????? ??????");
        alt_bld.setItems(R.array.select_photo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                // Toast.makeText(ProfileActivity.this, PhotoModels[item] + "??? ?????????????????????.", Toast.LENGTH_SHORT).show();
                if (item == 0) { // ?????????
                    capture();
                    alt_bld.create().dismiss();


                } else if (item == 1) { // ?????????
                    gallery();
                    alt_bld.create().dismiss();
                }
            }
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    // ?????? uri ?????? ?????? ???????????? ??????
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

    // content uri??? ??????????????? ??????
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

    // ????????????????????? ????????? ?????? ??? ?????? ???????????? ??????
    private void update_Photo(){
        // storage
        FirebaseStorage storage = FirebaseStorage.getInstance(); // ???????????? ???????????? ??????
        StorageReference storageReference = storage.getReference(); // ???????????? ??????
        // ????????? ?????????
        String filename = "profile" + uid + ".jpg";
        Uri file = photoUri;
        Log.d("uri", String.valueOf(file));
        StorageReference riversRef = storageReference.child("profile_img/"+filename);
        UploadTask uploadTask = riversRef.putFile(file);


        // ?????? ????????? ??????
        StorageReference desertRef = storageReference.child("profile_img/"+"profile"+ uid + ".jpg"); // ????????? ????????????
        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
            }
        });

        // ????????? ????????? ??????
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

    // ?????????????????? ?????????????????? ????????? ????????????
    private void getFireBaseProfileImage(String uid){
        File file = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/profile_img"); // ???????????? ????????? ??? ?????? ????????????
        // ????????? ??? ?????? /toolbar_images????????? ?????????
        // ??? ???????????? ??? ??????????????? ????????? ??????
        if(!file.isDirectory()){ // ??????????????? ?????????
            file.mkdir(); // ???????????? ??????
        }
        downloadImg(uid);
    }

    // ????????? ?????????????????? ????????????
    private void downloadImg(String uid){
        FirebaseStorage storage = FirebaseStorage.getInstance(); // ???????????? ???????????? ??????
        StorageReference storageReference = storage.getReference(); // ???????????? ??????
        storageReference.child("profile_img/"+"profile"+uid+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.d("??????", String.valueOf(uri));
                Glide.with(getApplicationContext()).load(uri).into(iv_user_img);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });
    }

    private void image_soltlux(){
        ImageAsyncTask_profile task=new ImageAsyncTask_profile();
        //base64 ????????? File f ->String strBase64??? ??????
//        File f = new File("/sdcard/Pictures/pic1.jpg");



        byte[] bt = new byte[(int) file1.length()];
        FileInputStream fis = null;
        String strBase64 = "";
        String id = "owner";
        try {
            fis = new FileInputStream(file1);
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
        //???????????? API ??????
        try {
            simil=task.execute(strBase64,id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Toast.makeText(getApplicationContext(), "" +simil , Toast.LENGTH_LONG).show();
    }




}