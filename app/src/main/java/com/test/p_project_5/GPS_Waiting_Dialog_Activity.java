package com.test.p_project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

public class GPS_Waiting_Dialog_Activity extends Dialog {

    public GPS_Waiting_Dialog_Activity(Context context){
        super(context);
        // 다이얼로그 제목 안보이게
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_analysis_loading);

    }
}