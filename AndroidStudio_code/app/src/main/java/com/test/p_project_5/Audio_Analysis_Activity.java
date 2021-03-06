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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
    Context cThis;//context ??????
    String LogTT="[STT]";//LOG?????????
    //?????? ?????????
    Intent SttIntent;
    SpeechRecognizer mRecognizer;
    //?????? ?????????
    TextToSpeech tts;
    private String uid;
    String sen1, sen2, reason;
    Float simil = null;


    // ??????????????????
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;

    // ?????? ?????????
    int randomNum;
    String now_sentence, now_voice;
    Button btn_audio_analysis;
    ImageButton btnSttStart;
    EditText txtInMsg;
    EditText txtSystem;
    TextView tv_audio_analysis;
    String[] strData = {"????????? ???????????????", "????????? ???????????????", "????????? ???????????????", "????????? ???????????????", "????????? ???????????????"};

    // ??????
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

        customProgressDialog = new Analysis_Loading_Activity(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // ???????????? ????????? ?????? ????????????
        mAuth = FirebaseAuth.getInstance(); // ?????? ?????? ?????? ????????????
        mDatabase = FirebaseDatabase.getInstance().getReference(); // ?????????????????? realtim database?????? ?????? ????????????






        // ?????? ?????? ??? ?????? ?????? ??????
        btn_audio_analysis = findViewById(R.id.btn_audio_analysis);
        btn_audio_analysis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sen1 = txtInMsg.getText().toString();
                sen2 = tv_audio_analysis.getText().toString();
                String s1, s2;
                s1 = "??????????????? ??????????????????";
                s2 = "????????? ??????????????????";
                JSONTask jsontask = new JSONTask();
                try {
                    simil = jsontask.execute(sen1, sen2).get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(simil>=80.0){
                    customProgressDialog.show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Audio_Analysis_Activity.this, Drunk_No_Activity.class);
                            intent.putExtra("uid", uid); // ????????? ?????? uid
                            startActivity(intent);
                        }
                    }, 2500);

                }
                else if(simil<79.9){
                    reason = "?????? ??????";
                    customProgressDialog.show();
                    Handler mHandler = new Handler();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(Audio_Analysis_Activity.this, Drunk_Yes_Activity.class);
                            intent.putExtra("uid", uid); // ????????? ?????? uid
                            intent.putExtra("??????", reason);
                            startActivity(intent);
                        }
                    }, 2500);

                }
            }
        });

        txtInMsg=(EditText)findViewById(R.id.txtInMsg);
        txtSystem=(EditText)findViewById(R.id.txtSystem);

        //????????????
        SttIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        SttIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,getApplicationContext().getPackageName());
        SttIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ko-KR");//????????? ??????
        mRecognizer=SpeechRecognizer.createSpeechRecognizer(cThis);
        mRecognizer.setRecognitionListener(listener);

        //???????????? ??????, ????????? ?????????
        tts=new TextToSpeech(cThis, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status!=android.speech.tts.TextToSpeech.ERROR){
                    tts.setLanguage(Locale.KOREAN);
                }
            }
        });
        //????????????
        btnSttStart=(ImageButton)findViewById(R.id.btn_stt_start);
        btnSttStart.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                System.out.println("???????????? ??????!");
                txtInMsg.setText("");
                btnSttStart.setImageDrawable(getResources().getDrawable(R.drawable.record_ing, null));
                txtSystem.setText("??????????????????.");
                if(ContextCompat.checkSelfPermission(cThis, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Audio_Analysis_Activity.this,new String[]{Manifest.permission.RECORD_AUDIO},1);
                    //????????? ???????????? ?????? ??????
                }else{
                    //????????? ????????? ??????
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
//            txtSystem.setText("???????????? ?????? ????????????..........."+"\r\n"+txtSystem.getText());
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
//            txtSystem.setText("????????? ?????? ?????? ?????????..........."+"\r\n"+txtSystem.getText());
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onResults(Bundle results) {
            btnSttStart.setImageDrawable(getResources().getDrawable(R.drawable.record, null));
            txtSystem.setText("?????? ??????");
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
    //????????? ?????? ????????? ?????? ??? ?????? ??????
    private void FuncVoiceOrderCheck(String VoiceMsg){
        if(VoiceMsg.length()<1)return;

        VoiceMsg=VoiceMsg.replace(" ","");//????????????

        if(VoiceMsg.indexOf("????????????")>-1 || VoiceMsg.indexOf("??????")>-1){
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.kakao.talk");
            startActivity(launchIntent);
            onDestroy();
        }//???????????? ????????? ??????
        if(VoiceMsg.indexOf("?????????")>-1 || VoiceMsg.indexOf("??????")>-1){
            FuncVoiceOut("????????? ?????????");//????????? ????????? ?????? ?????? ??????
        }
    }



    //?????? ????????? ?????????
    private void FuncVoiceOut(String OutMsg){
        if(OutMsg.length()<1)return;

        tts.setPitch(1.0f);//????????? ???1.0
        tts.setSpeechRate(1.0f);//????????? ??????
        tts.speak(OutMsg,TextToSpeech.QUEUE_FLUSH,null);

        //????????? ??????????????? ????????? ??????

    }
    //???????????? ????????? ????????? ???????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ?????? ??????
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
//            // URL ??????
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
//            // Header ?????? ??????
//            connection.addRequestProperty("Content-Type", "application/json");
//            connection.setDoOutput(true);
//            connection.setDoInput(true);
//
//            System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
//
//            JSONObject jsonBody = new JSONObject();
//            // ????????? ???
//            jsonBody.put("key", "9fd1c735-3dc2-4c4d-9410-2b05202705b4");
//            // ????????? ID
//            jsonBody.put("serviceId", "01984312263");
//
//            // ??????????????? ????????? ?????? parameter
//            JSONObject argument = new JSONObject();
//            argument.put("modelType","muse");
//            argument.put("sentence1","??????");
//            argument.put("sentence2","???????????????");
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
//            Log.d("??????????????? : ",e.toString());
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