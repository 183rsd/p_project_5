package com.test.p_project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private TextView tv_user_name;
    private ImageView iv_user_profile;
    private FirebaseAuth mAuth ;
    private BackPressHandler backPressHandler = new BackPressHandler(this);
    // ㅇㅇ


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String nickname = intent.getStringExtra("nickname");
        String photoUrl = intent.getStringExtra("photoUrl");
        String nickname_login = intent.getStringExtra("userName");
        mAuth = FirebaseAuth.getInstance();

        tv_user_name = findViewById(R.id.tv_user_name);
        if(nickname == null){
            tv_user_name.setText(nickname_login);
        }
        else{
            tv_user_name.setText(nickname);
        }

        iv_user_profile = findViewById(R.id.iv_user_profile);
        if(photoUrl != null){
            Glide.with(this).load(photoUrl).into(iv_user_profile);
        }

    }

    private void signOut_user(){ // 어플계정 로그아웃
        Toast.makeText(this, "로그인 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void signOut_google() { // 구글계정 로그아웃 (2년전꺼 참고해서 아마 코드 수정이 필요할 듯)
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "로그인 화면으로 돌아갑니다.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed(){
        backPressHandler.onBackPressed("'뒤로' 버튼 한번 더 누르시면 종료됩니다.",3000);
    }
}