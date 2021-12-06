package com.test.p_project_5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class Drunk_Yes_Activity extends AppCompatActivity{
    private TextView drunk_tv_call1, drunk_tv_call2;
    private Button drunk_btn_call1_dialog,drunk_btn_call2_dialog;

    String uid, call1, call2, tel;

    // 파이어베이스
    private FirebaseAuth mAuth ;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drunk_yes);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // 로그인한 유저의 정보 가져오기
        mAuth = FirebaseAuth.getInstance(); // 유저 계정 정보 가져오기
        mDatabase = FirebaseDatabase.getInstance().getReference(); // 파이어베이스 realtim database에서 정보 가져오기

        readUser(uid);

        drunk_tv_call1 = (TextView) findViewById(R.id.drunk_tv_call1);
        drunk_tv_call2 =(TextView) findViewById(R.id.drunk_tv_call2);

        drunk_btn_call1_dialog = findViewById(R.id.drunk_btn_call1_dialog);
        drunk_btn_call2_dialog = findViewById(R.id.drunk_btn_call2_dialog);

        drunk_btn_call1_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tel = "tel:" + call1;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                startActivity(intent);
            }
        });
        drunk_btn_call2_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tel = "tel:" + call2;
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(tel));
                startActivity(intent);
            }
        });


    }



    // 파이어베이스 데이터 조회
    private void readUser(String userId){
        mDatabase.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue(UserData.class)!=null){
                    UserData post = snapshot.getValue(UserData.class);
                    drunk_tv_call1.setText(post.getCall1());
                    drunk_tv_call2.setText(post.getCall2());

                    call1 = drunk_tv_call1.getText().toString();
                    call2 = drunk_tv_call2.getText().toString();


                } else{
                    drunk_tv_call1.setText("설정안함");
                    drunk_tv_call2.setText("설정안함");
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}