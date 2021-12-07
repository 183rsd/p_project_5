package com.test.p_project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Voice_Analysis_Activity extends AppCompatActivity {

    private ImageButton btn_voice_record;
    TextView tv_record, tv_record_file;

    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 생성 파일 이름
    private boolean isRecording = false; // 현재 녹음 상태
    private Uri audioUri = null; // 오디오 파일 uri

    private ArrayList<Uri> audioList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 화면을 landscape(가로) 화면으로 고정
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_voice_analysis);

        btn_voice_record = findViewById(R.id.btn_voice_record);
        tv_record = findViewById(R.id.tv_record);
        tv_record_file = findViewById(R.id.tv_record_file);

        btn_voice_record.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if(isRecording){ // 녹음 중이라면, 녹음 중지
                    isRecording = false;
                    btn_voice_record.setImageDrawable(getResources().getDrawable(R.drawable.record, null));
                    tv_record.setText("녹음 완료");
                    stopRecording();
                }
                else{ // 녹음 중이 아니라면, 녹음 시작
                    if(checkAudioPermission()){
                        isRecording = true;
                        btn_voice_record.setImageDrawable(getResources().getDrawable(R.drawable.record_ing, null));
                        tv_record.setText("녹음 중");
                        startRecording();
                    }
                }
            }
        });
    }

    // 오디오 파일 권한 체크
    private boolean checkAudioPermission(){
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED){
            return true;
        } else{
            ActivityCompat.requestPermissions(this, new String[] {recordPermission}, PERMISSION_CODE);
            return false;
        }
    }

    // 녹음 시작
    private void startRecording(){
        // 파일 외부 경로 확인
        String recordPath = getExternalFilesDir("/").getAbsolutePath(); // 파일 외부 경로 확인
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()); // 파일이름 변수를 현재 날짜
        audioFileName = recordPath + "/" + "RecordExample_" + timeStamp + "_"+"audio.mp4";

        //Media Recorder 생성 및 설정
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(audioFileName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //녹음 시작
        mediaRecorder.start();
    }

    // 녹음 종료
    private void stopRecording(){
        // 녹음 종료
        mediaRecorder.stop();
        mediaRecorder.release();
        mediaRecorder = null;

        // 파일 경로(String) 값을 Uri로 변환해서 저장
        //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
        //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
        audioUri = Uri.parse(audioFileName);
        tv_record_file.setText(audioUri.toString());

    }
}