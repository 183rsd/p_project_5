package com.test.p_project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_id, register_pass, register_pass_check, register_nickname;
    private Button register_btn_success;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_id = findViewById(R.id.register_id);
        register_pass = findViewById(R.id.register_pass);
        register_pass_check = findViewById(R.id.register_pass_check);
        register_nickname = findViewById(R.id.register_nickname);
        register_btn_success = findViewById(R.id.register_btn_success);

        register_btn_success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = register_id.getText().toString();
                String userPW = register_pass.getText().toString();
                String userPW_check = register_pass_check.getText().toString();
                String userName = register_nickname.getText().toString();


                if(userID.equals("") || userPW.equals("") || userPW_check.equals("") || userName.equals("")){
                    Toast.makeText(getApplicationContext(), "빈칸없이 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(!userPW.equals(userPW_check)) {
                    Toast.makeText(getApplicationContext(), "비밀번호를 다시 확인해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success"); // Register.php파일에서 성공시 "success"를 반환하도록 지정
                                if(success) { // 회원가입 성공
                                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } // 회원가입 실패
                                else{
                                    Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    // volley를 이용해서 서버로 요청
                    RegisterRequest registerRequest = new RegisterRequest(userID, userPW, userName, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);
                }



            }
        });
    }
    // 키보드 숨기기
    private void hideKeyboard(){
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(register_id.getWindowToken(),0);
        imm.hideSoftInputFromWindow(register_pass.getWindowToken(),0);
        imm.hideSoftInputFromWindow(register_pass_check.getWindowToken(),0);
        imm.hideSoftInputFromWindow(register_nickname.getWindowToken(),0);
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
}