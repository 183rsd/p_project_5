package com.test.p_project_5;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Typing_Test_Activity extends AppCompatActivity {
    Button btn1;
    TextView textViewTime, warning, random_text, count, life, notwarning;
    EditText edit1;
    private long timeCountInMilliSeconds = 1 * 60000;
    private CountDownTimer countDownTimer;
    private int x=0, y=5;
    int randomNum;
    String[] strData = {"안녕하세요1", "안녕하세요2", "안녕하세요3", "안녕하세요4", "안녕하세요5"};
    private long backKeyPressedTime = 0;
    //다음 액티비티로 넘어가기 위해 먼저 자기 자신을 선언
    public static Activity typing_Test_Activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 landscape(가로) 화면으로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_typing_test);
        //다음 액티비티로 넘어가기 위해 먼저 자기 자신을 객체에 담음
        //넘어갈 액티비티에는
        //Typing_Test_Activity typing_Test_Activity = (Typing_Test_Activity)Typing_Test_Activity.typing_Test_Activity;
        //typing_Test_Activity.finish();
        //를 현재 위치에 추가해 준다
        typing_Test_Activity=Typing_Test_Activity.this;

        btn1=(Button) findViewById(R.id.btn1);
        textViewTime=(TextView) findViewById(R.id.textViewTime);
        count=(TextView) findViewById(R.id.count);
        life=(TextView) findViewById(R.id.life);
        notwarning=(TextView) findViewById(R.id.notwarning);
        warning=(TextView) findViewById(R.id.warning);
        edit1=(EditText) findViewById(R.id.edit1);
        random_text = (TextView)findViewById(R.id.random_text);
        //랜덤문자열 생성
        Random rnd = new Random();



        final String random_name_1 = "1번문장"; //한글 랜덤 문자 1
        final String random_name_2 = "2번문장"; //한글 랜덤 문자 2
        final String random_name_3 = "3번문장"; //한글 랜덤 문자 3
        final String random_name_4 = "4번문장"; //한글 랜덤 문자 4
        final String random_name_5 = "5번문장"; //한글 랜덤 문자 5
        final String random_name_6 = "6번문장"; //한글 랜덤 문자 6
        final String random_name_7 = "7번문장"; //한글 랜덤 문자 7
        final String random_name_8 = "8번문장"; //한글 랜덤 문자 8
        final String random_name_9 = "9번문장"; //한글 랜덤 문자 9
        final String random_name_10 = "10번문장"; //한글 랜덤 문자 10

        final Button btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                //입력칸, 시간, 문자열 활성화
                btn_start.setVisibility(View.INVISIBLE);
                btn1.setVisibility(View.VISIBLE);
                textViewTime.setVisibility(View.VISIBLE);
                edit1.setVisibility(View.VISIBLE);

                randomText();

                //카운트 다운 시간 30초
                String conversionTime = "000200";
                // 카운트 다운 시작
                countDown(conversionTime);
            }
        });

        btn1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //입력칸, 시간, 문자열 활성화
                warning.setVisibility(View.VISIBLE);
                notwarning.setVisibility(View.VISIBLE);
                life.setVisibility(View.VISIBLE);
                count.setVisibility(View.VISIBLE);
                if(random_text.getText().toString().equals(edit1.getText().toString())){
                    //성공 결과로 넘어가는 액티비티 구현해야함
                    Intent intent1=new Intent(getApplicationContext(), Alcohol_Detected_Activity.class);
                    startActivity(intent1);
                } else{
                    x++;
                    y--;
                    count.setText(x+"");
                    life.setText(y+"");
                }
                if (count.getText().toString().equals("5")){
                    Intent intent1=new Intent(Typing_Test_Activity.this, Repeat_Fail_Activity.class);
                    startActivity(intent1);
                }
            }
        });
    }

    private void randomText(){
//        randomNum = (int) (Math.random() * 10);
        Random random = new Random();

        randomNum = random.nextInt(5);
        random_text.setText(strData[randomNum]);

    }

    public void countDown(String time) {

        long conversionTime = 0;
        // 1000 단위가 1초
        // 60000 단위가 1분
        // 60000 * 3600 = 1시간
        String getHour = time.substring(0, 2);
        String getMin = time.substring(2, 4);
        String getSecond = time.substring(4, 6);

        // "00"이 아니고, 첫번째 자리가 0 이면 제거
        if (getHour.substring(0, 1) == "0") {
            getHour = getHour.substring(1, 2);
        }
        if (getMin.substring(0, 1) == "0") {
            getMin = getMin.substring(1, 2);
        }
        if (getSecond.substring(0, 1) == "0") {
            getSecond = getSecond.substring(1, 2);
        }

        // 변환시간
        conversionTime = Long.valueOf(getHour) * 1000 * 3600 + Long.valueOf(getMin) * 60 * 1000 + Long.valueOf(getSecond) * 1000;

        // 첫번쨰 인자 : 원하는 시간 (예를들어 30초면 30 x 1000(주기))
        // 두번쨰 인자 : 주기( 1000 = 1초)
        new CountDownTimer(conversionTime, 1000) {

            // 특정 시간마다 뷰 변경
            public void onTick(long millisUntilFinished) {
                // 시간단위
                String hour = String.valueOf(millisUntilFinished / (60 * 60 * 1000));
                // 분단위
                long getMin = millisUntilFinished - (millisUntilFinished / (60 * 60 * 1000)) ;
                String min = String.valueOf(getMin / (60 * 1000)); // 몫
                // 초단위
                String second = String.valueOf((getMin % (60 * 1000)) / 1000); // 나머지
                // 밀리세컨드 단위
                String millis = String.valueOf((getMin % (60 * 1000)) % 1000); // 몫
                // 시간이 한자리면 0을 붙인다
                if (hour.length() == 1) {
                    hour = "0" + hour;
                }
                // 분이 한자리면 0을 붙인다
                if (min.length() == 1) {
                    min = "0" + min;
                }
                // 초가 한자리면 0을 붙인다
                if (second.length() == 1) {
                    second = "0" + second;
                }

                textViewTime.setText(hour + ":" + min + ":" + second);
            }

            // 제한시간 종료시
            public void onFinish() {
                // 변경 후
                textViewTime.setText("시간초과!!!");
                //시간초과후 실패결과화면으로 이동하는 코드 넣기!
                Intent intent1=new Intent(Typing_Test_Activity.this, Repeat_Fail_Activity.class);
                startActivity(intent1);
            }
        }.start();
    }

}
