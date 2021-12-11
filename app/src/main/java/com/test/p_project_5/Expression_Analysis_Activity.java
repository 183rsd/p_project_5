package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Expression_Analysis_Activity extends AppCompatActivity {
    Button expression_success, expression_fail, go_voice;
    String uid, now_user, reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expression_analysis);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        now_user = intent.getStringExtra("현재사용자");


        // 표정분석 성공
        expression_success = findViewById(R.id.expression_success);
        expression_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"테스트 통과. 다음 테스트 진행",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(Expression_Analysis_Activity.this, Typing_Test_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                intent.putExtra("현재사용자", now_user); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        expression_fail = findViewById(R.id.expression_fail);
        expression_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = "표정 분석";
                Intent intent = new Intent(Expression_Analysis_Activity.this, Drunk_Yes_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                intent.putExtra("현재사용자", now_user); // 사용자 고유 uid
                intent.putExtra("이유", reason);
                startActivity(intent);
            }
        });

        go_voice = findViewById(R.id.go_voice);
        go_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Expression_Analysis_Activity.this, Audio_Analysis_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                intent.putExtra("현재사용자", now_user); // 사용자 고유 uid
                intent.putExtra("이유", reason);
                startActivity(intent);
            }
        });
    }
}