package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Drunk_No_Activity extends AppCompatActivity {
    Button btn_app_close;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drunk_no);

        btn_app_close = findViewById(R.id.btn_app_close);
        btn_app_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAffinity(Drunk_No_Activity.this);
                System.exit(0);
            }
        });

    }
}