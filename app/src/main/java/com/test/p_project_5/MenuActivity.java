package com.test.p_project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MenuActivity extends AppCompatActivity {
    private Button btn_profile_update, btn_version_info, btn_help, btn_logout, btn_alcohol_detect, btn_drunk_yes, btn_drunk_no, btn_repeat_fail;
    private String uid;
    private ImageView iv_user_profile;
    private TextView tv_user_name;

    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
    private BackPressHandler backPressHandler = new BackPressHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        String photoUrl = intent.getStringExtra("photoUrl");

        iv_user_profile = findViewById(R.id.iv_user_profile);
        tv_user_name = findViewById(R.id.tv_user_name);
        tv_user_name.setText(nickname);

        if(photoUrl != null){
            Glide.with(this).load(photoUrl).into(iv_user_profile);
        }


        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        uid = user != null ? user.getUid() : null; // 로그인한 유저의 고유 uid 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기


        // 프로빌 수정 버튼 클릭
        btn_profile_update = findViewById(R.id.btn_profile_update);
        btn_profile_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, HomeActivity.class);
                intent.putExtra("nickname", nickname); // 닉네임
                intent.putExtra("photoUrl", photoUrl); // 프로필사진
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        // 버전 정보 버튼 클릭
        btn_version_info = findViewById(R.id.btn_version_info);
        btn_version_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, VersionInfoActivity.class);
                startActivity(intent);
            }
        });

        // 도움말 버튼 클릭
        btn_help = findViewById(R.id.btn_help);
        btn_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        // 로그아웃 버튼 클릭
        btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut_google();
            }
        });

        // 알코올 감지 버튼 클릭
        btn_alcohol_detect = findViewById(R.id.btn_alcohol_detect);
        btn_alcohol_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Alcohol_Detected_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        // 음주 상태 버튼 클릭
        btn_drunk_yes = findViewById(R.id.btn_drunk_yes);
        btn_drunk_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Drunk_Yes_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        // 미음주 상태 버튼 클릭
        btn_drunk_no = findViewById(R.id.btn_drunk_no);
        btn_drunk_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Drunk_No_Activity.class);
                startActivity(intent);
            }
        });

        btn_repeat_fail = findViewById(R.id.btn_repeat_fail);
        btn_repeat_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuActivity.this, Repeat_Fail_Activity.class);
                startActivity(intent);
            }
        });
    }


    private void signOut_google() { // 구글계정 로그아웃 (2년전꺼 참고해서 아마 코드 수정이 필요할 듯)
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "로그인 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed(){
        backPressHandler.onBackPressed("'뒤로' 버튼 한번 더 누르시면 종료됩니다.",3000);
    }


}