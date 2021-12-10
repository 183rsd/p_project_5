package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Stried_Analysis_Activity extends AppCompatActivity {
    Button stried_analysis_success, stried_analysis_fail;
    String uid, now_user, reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stried_analysis);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        now_user = intent.getStringExtra("현재사용자");

        stried_analysis_success = findViewById(R.id.stried_analysis_success);
        stried_analysis_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), Drunk_No_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                startActivity(intent);
            }
        });

        stried_analysis_fail = findViewById(R.id.stried_analysis_fail);
        stried_analysis_fail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reason = "보폭 분석";
                Intent intent=new Intent(getApplicationContext(), Drunk_Yes_Activity.class);
                intent.putExtra("uid", uid); // 사용자 고유 uid
                intent.putExtra("이유", reason);
                startActivity(intent);
            }
        });


    }
}