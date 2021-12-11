package com.test.p_project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.conscrypt.Conscrypt;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Audio_Analysis_Activity extends AppCompatActivity {
    Context cThis;//context 설정
    String LogTT="[STT]";//LOG타이틀
    //음성 인식용
    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    //음성 출력용
    TextToSpeech tts;
    private String uid;
    String sen1, sen2, reason;
    Float simil = null;

    // 파이어베이스
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;

    // 화면 처리용
    int randomNum;
    String now_sentence, now_voice;
    Button btn_audio_analysis;
    ImageButton btnSttStart;
    EditText txtInMsg;
    EditText txtSystem;
    TextView tv_audio_analysis;
    String[] strData = {"안", "안녕", "안녕하", "안녕하세", "안녕하세요"};

    // 로딩
    Analysis_Loading_Activity customProgressDialog;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void onCreate(Bundle savedInstanceState) {
        cThis=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_analysis);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");


        tv_audio_analysis = findViewById(R.id.tv_audio_analysis);
        randomText();

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기






        // 음성 녹음 후 다음 버튼 클릭
        btn_audio_analysis = findViewById(R.id.btn_audio_analysis);
        btn_audio_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sen1 = txtInMsg.getText().toString();
                sen2 = tv_audio_analysis.getText().toString();
                String s1, s2;
                s1 = "안녕하세요 전민종입니다";
                s2 = "안녕하 저미농입니다";
                JSONTask jsontask = new JSONTask();
                try {
                    simil = jsontask.execute(sen1, sen2).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(simil>=80.0){
                    Intent intent = new Intent(Audio_Analysis_Activity.this, Drunk_No_Activity.class);
                    intent.putExtra("uid", uid); // 사용자 고유 uid
                    startActivity(intent);
                }
                else if(simil<79.9){
                    reason = "음성 분석";
                    Intent intent = new Intent(Audio_Analysis_Activity.this, Drunk_Yes_Activity.class);
                    intent.putExtra("uid", uid); // 사용자 고유 uid
                    intent.putExtra("이유", reason);
                    startActivity(intent);
                }
            }
        });

        txtInMsg=(EditText)findViewById(R.id.txtInMsg);
        txtSystem=(EditText)findViewById(R.id.txtSystem);

        //음성인식
        SttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//한국어 사용
        mRecognizer=SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        //음성출력 생성, 리스너 초기화
        tts=new TextToSpeech(cThis, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        //버튼설정
        btnSttStart=(ImageButton)findViewById(R.id.btn_stt_start);
        btnSttStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                System.out.println("음성인식 시작!");
                txtInMsg.setText("");
                btnSttStart.setImageDrawable(getResources().getDrawable(R.drawable.record_ing, null));
                txtSystem.setText("녹음중입니다.");
                if(ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Audio_Analysis_Activity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                    //권한을 허용하지 않는 경우
                }else{
                    //권한을 허용한 경우
                    try {
                        mRecognizer.startListening(SttIntent);
                    }catch (SecurityException e){e.printStackTrace();}
                }
            }
        });


    }


    private RecognitionListener listener=new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle bundle) {
//            txtSystem.setText("onReadyForSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onBeginningOfSpeech() {
//            txtSystem.setText("지금부터 말을 해주세요..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onRmsChanged(float v) {

        }

        @Override
        public void onBufferReceived(byte[] bytes) {
//            txtSystem.setText("onBufferReceived..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEndOfSpeech() {
//            txtSystem.setText("onEndOfSpeech..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onError(int i) {
//            txtSystem.setText("천천히 다시 말해 주세요..........."+"\r\n"+txtSystem.getText());
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onResults(Bundle results) {
            btnSttStart.setImageDrawable(getResources().getDrawable(R.drawable.record, null));
            txtSystem.setText("녹음 완료");
            String key= "";
            key = SpeechRecognizer.RESULTS_RECOGNITION;
            ArrayList<String> mResult =results.getStringArrayList(key);
            String[] rs = new String[mResult.size()];
            mResult.toArray(rs);
            txtInMsg.setText(rs[0]+"\r\n"+txtInMsg.getText());
            sen1 = txtInMsg.getText().toString();
            FuncVoiceOrderCheck(rs[0]);
            mRecognizer.startListening(SttIntent);
            btn_audio_analysis.setEnabled(true);

        }

        @Override
        public void onPartialResults(Bundle bundle) {
            txtSystem.setText("onPartialResults..........."+"\r\n"+txtSystem.getText());
        }

        @Override
        public void onEvent(int i, Bundle bundle) {
            txtSystem.setText("onEvent..........."+"\r\n"+txtSystem.getText());
        }
    };
    //입력된 음성 메세지 확인 후 동작 처리
    private void FuncVoiceOrderCheck(String VoiceMsg){
        if(VoiceMsg.length()<1)return;

        VoiceMsg=VoiceMsg.replace(" ","");//공백제거

        if(VoiceMsg.indexOf("카카오톡")>-1 || VoiceMsg.indexOf("카톡")>-1){
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.kakao.talk");
            startActivity(launchIntent);
            onDestroy();
        }//카카오톡 어플로 이동
        if(VoiceMsg.indexOf("전동꺼")>-1 || VoiceMsg.indexOf("불꺼")>-1){
            FuncVoiceOut("전등을 끕니다");//전등을 끕니다 라는 음성 출력
        }
    }



    //음성 메세지 출력용
    private void FuncVoiceOut(String OutMsg){
        if(OutMsg.length()<1)return;

        tts.setPitch(1.0f);//목소리 톤1.0
        tts.setSpeechRate(1.0f);//목소리 속도
        tts.speak(OutMsg,TextToSpeech.QUEUE_FLUSH,null);

        //어플이 종료할때는 완전히 제거

    }
    //카톡으로 이동을 했는데 음성인식 어플이 종료되지 않아 계속 실행되는 경우를 막기위해 어플 종료 함수
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(tts!=null){
            tts.stop();
            tts.shutdown();
            tts=null;
        }
        if(mRecognizer!=null){
            mRecognizer.destroy();
            mRecognizer.cancel();
            mRecognizer=null;
        }
    }

    private void randomText(){
//        randomNum = (int) (Math.random() * 10);
        Random random = new Random();

        randomNum = random.nextInt(5);
        tv_audio_analysis.setText(strData[randomNum]);
        now_sentence = strData[randomNum];

    }

//    public static float apiServiceTest(String sen1,String sen2) {
//        System.setProperty("https.protocols","TLSv1.2");
//        URL url = null;
//        URLConnection connection = null;
//        StringBuilder responseBody = new StringBuilder();
//        JSONParser jsonparse=new JSONParser();
//
//        float num = 10;
//        try {
//            // URL 지정
//            url = new URL("http://svc.saltlux.ai:31781");
//
////            TrustManager[] trustAllCerts = new TrustManager[]{
////                    new X509TrustManager() {
////
////                        public java.security.cert.X509Certificate[] getAcceptedIssuers()
////                        {
////                            return null;
////                        }
////                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
////                        {
////                            //No need to implement.
////                        }
////                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
////                        {
////                            //No need to implement.
////                        }
////                    }
////            };
////
////// Install the all-trusting trust manager
////            try
////            {
////                SSLContext sc = SSLContext.getInstance("SSL");
////                sc.init(null, trustAllCerts, new java.security.SecureRandom());
////                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
////            }
////            catch (Exception e)
////            {
////                System.out.println(e);
////            }
//
//            connection = url.openConnection();
//            // Header 정보 지정
//            connection.addRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//
//            JSONObject jsonBody = new JSONObject();
//            // 사용자 키
//            jsonBody.put("key", "9fd1c735-3dc2-4c4d-9410-2b05202705b4");
//            // 서비스 ID
//            jsonBody.put("serviceId", "01984312263");
//
//            // 서비스에서 필요로 하는 parameter
//            JSONObject argument = new JSONObject();
//            argument.put("modelType","muse");
//            argument.put("sentence1","안녕");
//            argument.put("sentence2","안녕하세여");
//            jsonBody.put("argument", argument);
//
//            BufferedOutputStream bos = new BufferedOutputStream(connection.getOutputStream());
//
//            bos.write(jsonBody.toJSONString().getBytes(StandardCharsets.UTF_8));
//            bos.flush();
//            bos.close();
//
//            BufferedReader br = new BufferedReader(
//                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
//            String line = null;
//            while ((line = br.readLine()) != null) {
//                responseBody.append(line);
//            }
//            br.close();
//
//        } catch (Throwable e) {
////            System.out.println(e.toString());
//            Log.d("에러ㄴㄻㅇ : ",e.toString());
//        }
//
//
//        String result=responseBody.toString();
//        try {
//            JSONObject jsonarray=(JSONObject)jsonparse.parse(result);
//            JSONObject simil=(JSONObject)jsonarray.get("return_object");
//            String i=simil.get("similarity").toString();
//            num=Float.parseFloat(i)*100;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return num;
//    }

}