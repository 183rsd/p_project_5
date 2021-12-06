package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Repeat_Fail_Activity extends AppCompatActivity {

    private TextView tv_countdown;
    private int minute, second;
    private Button btn_test_repeat;

    private  static final long START_TIME_IN_MILLIS = 300000; // 5분 시간 설정
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat_fail);


        tv_countdown = (TextView)findViewById(R.id.tv_countdown);
        btn_test_repeat = (Button)findViewById(R.id.btn_test_repeat);

        btn_test_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Repeat_Fail_Activity.this, Alcohol_Detected_Activity.class);
                startActivity(intent);
            }
        });
        startTimer();

    }
    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                btn_test_repeat.setEnabled(true);
            }
        }.start();
    }
    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
        tv_countdown.setText(timeLeftFormatted);
    }

}