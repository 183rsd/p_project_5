package com.test.p_project_5;

import android.net.Uri;

public class UserData {
//    private String idToken; // Firebase uid
    private String call1 = ""; // 연락망1
    private String call2 = ""; // 연락망2
//    private Uri userPhoto; // 프로필사진

    public UserData(){}
    public UserData(String call1, String call2) {
//        this.idToken = idToken;
        this.call1 = call1;
        this.call2 = call2;
//        this.userPhoto = userPhoto;

    }
//    public String getIdToken() { return idToken; }
    public String getCall1() { return call1; }
    public String getCall2() { return call2; }
//    public Uri getUserPhoto() { return userPhoto; }

//    public void setIdToken(String idToken) { this.idToken = idToken; }
    public void setCall1(String call1) { this.call1 = call1; }
    public void setCall2(String call2) { this.call2 = call2; }
}
