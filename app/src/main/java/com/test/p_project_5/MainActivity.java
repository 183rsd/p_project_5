package com.test.p_project_5;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private SignInButton btn_google;
    private GoogleApiClient googleApiClient; // 구글 api 클라이언트 객체
    private String TAG="mainTag";
    private FirebaseAuth mAuth; // 파이어베이스 인증 객체
    private int RC_SIGN_IN=123; // 구글 로그인 결과 코드

    private EditText login_id, login_pw;
    private Button btn_login, btn_register;
    private CheckBox autoLogin_check;
    private Context mContext;
    // 뒤로가기
    private BackPressHandler backPressHandler = new BackPressHandler(MainActivity.this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_id = findViewById(R.id.et_id);
        login_pw = findViewById(R.id.et_pass);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        autoLogin_check = findViewById(R.id.autoLogin_check);
        mContext = this;

        boolean boo = com.test.p_project_5.PreferenceManager.getBoolean(mContext,"check"); // 로그인 정보 기억하기 체크 유무 확인
        if(boo){ // 로그인 정보 기억하기 버튼이 체크되어 있으면
            // PreferencceManager에 저장된 id,pw값을 editText에 세팅
            login_id.setText(com.test.p_project_5.PreferenceManager.getString(mContext,"user_id"));
            login_pw.setText(com.test.p_project_5.PreferenceManager.getString(mContext,"password"));
            autoLogin_check.setChecked(true);
        }



        btn_register.setOnClickListener(new View.OnClickListener() { // 회원가입 버튼 클릭
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() { // 로그인 버튼 클릭
            @Override
            public void onClick(View view) {
                String userID = login_id.getText().toString();
                String userPW = login_pw.getText().toString();

                // user_id, password 값을 PreferenceManager에 저장 (자동로그인 데이터 저장)
                com.test.p_project_5.PreferenceManager.setString(mContext,"user_id",userID);
                com.test.p_project_5.PreferenceManager.setString(mContext,"password",userPW);

                // 저장한 키 값으로 저장된 아이디, 비밀번호를 불러와서 check_user_id, check_password에 저장
                com.test.p_project_5.PreferenceManager.getString(mContext,"user_id");
                com.test.p_project_5.PreferenceManager.getString(mContext,"password");

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success"); // Login.php파일에서 성공시 "success"를 반환하도록 지정
                            if(success) { // 로그인 성공
                                String userID = jsonObject.getString("userID");
                                String userPass = jsonObject.getString("userPassword");
                                String userName = jsonObject.getString("userName");

                                if(autoLogin_check.isChecked()){
                                    SharedPreferences auto = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
                                    SharedPreferences.Editor autoLoginEdit = auto.edit();
                                    autoLoginEdit.putString("user_id",userID);
                                    autoLoginEdit.putString("password",userPW);
                                    autoLoginEdit.commit();
                                }

                                Toast.makeText(getApplicationContext(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                intent.putExtra("userName", userName);
                                startActivity(intent);
                            } // 회원가입 실패
                            else{
                                Toast.makeText(getApplicationContext(), "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                // volley를 이용해서 서버로 요청
                LoginRequest loginRequest = new LoginRequest(userID, userPW, responseListener);
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                queue.add(loginRequest);
            }
        });


        // [START config_signin]
        // 구글로그인
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("607168936931-s8li936jdc7anppg9osn6kea8ss8bnt3.apps.googleusercontent.com")
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this) // fragment에서 구현시 this 대신 getAcitivity
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mAuth = FirebaseAuth.getInstance(); // 파이어베이스 인증 객체 초기화

        btn_google = findViewById(R.id.btn_google);
        btn_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });

        // 자동로그인 체크박스 유무에 따른 동작 구현
        autoLogin_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CheckBox)view).isChecked()){ // 체크박스 체크되어있으면
                    // editText에서 아이디, 비밀번호 가져와서 PreferenceManager에 저장
                    com.test.p_project_5.PreferenceManager.setString(mContext,"user_id",login_id.getText().toString());
                    com.test.p_project_5.PreferenceManager.setString(mContext,"password",login_pw.getText().toString());
                    com.test.p_project_5.PreferenceManager.setBoolean(mContext,"check",autoLogin_check.isChecked()); // 현재 체크박스 상태 저장
                } else{ // 체크박스 해제되어있으면
                    com.test.p_project_5.PreferenceManager.setBoolean(mContext,"check",autoLogin_check.isChecked()); // 현재 체크박스 상태 저장
                    com.test.p_project_5. PreferenceManager.clear(mContext); // 로그인 정보 모두 날림
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // 구글 로그인 인증 요청 시 결과 되돌려 받는 함수
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if(result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount(); // account변수에 구글로그인 데이터 저장(닉네임, 프로필사진 등)
                resultLogin(account); // 로그인 결과값 출력 메소드 호출
            }
        }
    }

    private void resultLogin(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ // 로그인 성공했을 때
                            Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            intent.putExtra("nickname", account.getDisplayName()); // 닉네임
                            intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); // 프로필사진 (포토url을 string으로 변환)

                            startActivity(intent);
                        } else { // 로그인 실패했을 때
                            Toast.makeText(MainActivity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    // 키보드 숨기기
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(login_id.getWindowToken(),0);
        imm.hideSoftInputFromWindow(login_pw.getWindowToken(),0);
    }

    // 화면 터치 시 키보드 내려감
    public boolean dispatchTouchEvent(MotionEvent ev){
        View focusView = getCurrentFocus();
        if(focusView != null){
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if(!rect.contains(x,y)){
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed(){
//        // Default
//        backPressHandler.onBackPressed();
//        // Toast 메세지 사용자 지정
//        backPressHandler.onBackPressed("'뒤로' 버튼 한번 더 누르시면 종료됩니다.");
//        // 뒤로가기 간격 사용자 지정
//        backPressHandler.onBackPressed(3000);
//        // Toast, 간격 사용자 지정
        backPressHandler.onBackPressed("'뒤로' 버튼 한번 더 누르시면 종료됩니다.",3000);
    }
}