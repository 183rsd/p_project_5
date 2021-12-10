package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TestActivity extends AppCompatActivity {
    private Button btn_alcohol_detect, btn_drunk_yes, btn_drunk_no, btn_repeat_fail, btn_voice_analysis, btn_typing_test;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();

        uid = intent.getStringExtra("uid");


        // 알코올 감지 버튼 클릭
        btn_alcohol_detect = findViewById(R.id.btn_alcohol_detect);
        btn_alcohol_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Alcohol_Detected_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        // 음주 상태 버튼 클릭
        btn_drunk_yes = findViewById(R.id.btn_drunk_yes);
        btn_drunk_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Drunk_Yes_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        // 미음주 상태 버튼 클릭
        btn_drunk_no = findViewById(R.id.btn_drunk_no);
        btn_drunk_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Drunk_No_Activity.class);
                startActivity(intent);
            }
        });

        btn_repeat_fail = findViewById(R.id.btn_repeat_fail);
        btn_repeat_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Repeat_Fail_Activity.class);
                startActivity(intent);
            }
        });

        btn_voice_analysis = findViewById(R.id.btn_voice_analysis);
        btn_voice_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Audio_Analysis_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        btn_typing_test = findViewById(R.id.btn_typing_test);
        btn_typing_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TestActivity.this, Typing_Test_Activity.class);
                startActivity(intent);
            }
        });
    }
}