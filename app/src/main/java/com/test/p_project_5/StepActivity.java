package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.binary.Base64;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class StepActivity extends AppCompatActivity {
    private TextView tv_step_load, tv_step_analysis, tv_step_analysis_finish;
    String uid, now_user;
    String step_result = "";
    // 로딩
    Analysis_Loading_Activity customProgressDialog;

    private final Handler handler = new Handler();

    // 타이머
    private  static final long START_TIME_IN_MILLIS = 3000; // 위치 정보 불러오기 위한 3초 시간 설정
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    int z=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        now_user = intent.getStringExtra("현재사용자");

        step_result = "보폭분석 통과";
        // step_result = "보폭분석 불통과";

        tv_step_load = findViewById(R.id.tv_step_load);
        tv_step_analysis = findViewById(R.id.tv_step_analysis);
        tv_step_analysis_finish = findViewById(R.id.tv_step_analysis_finish);

        customProgressDialog = new Analysis_Loading_Activity(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        StartTimer();
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
                                tv_step_analysis.setVisibility(View.VISIBLE);
                            }
                            if(String.valueOf(z).equals("2")){
                                tv_step_analysis_finish.setVisibility(View.VISIBLE);
                            }
                            if(String.valueOf(z).equals("3")){
                                if(step_result.equals("보폭분석 통과")){
                                    Intent intent = new Intent(StepActivity.this, Drunk_No_Activity.class);
                                    intent.putExtra("uid", uid); // 사용자 고유 uid
                                    intent.putExtra("현재사용자",now_user);
                                    startActivity(intent);
                                }
                                else if(step_result.equals("보폭분석 불통과")){
                                    Intent intent = new Intent(StepActivity.this, Drunk_Yes_Activity.class);
                                    intent.putExtra("uid", uid); // 사용자 고유 uid
                                    intent.putExtra("현재사용자",now_user);
                                    startActivity(intent);
                                }


                            }
                        }
                    });
                    try {
                        Thread.sleep(2500); //딜레이 타임 조절
                    }catch (
                            InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
        }).start();
    }


}