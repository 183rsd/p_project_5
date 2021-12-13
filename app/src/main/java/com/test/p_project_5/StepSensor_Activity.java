package com.test.p_project_5;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class StepSensor_Activity extends AppCompatActivity implements SensorEventListener {
    Handler handler = new Handler();
    // 로딩

    private GpsTracker gpsTracker;


    // 로딩 다이얼로그
    Analysis_Loading_Activity customProgressDialog;

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


    // 타이머
    private  static final long START_TIME_IN_MILLIS = 3000; // 위치 정보 불러오기 위한 3초 시간 설정
    private CountDownTimer mCountDownTimer;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    private Button button_stepsensor;
    private TextView tv_stepsensor;

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    TextView tvStepDetector;
    private int mStepDetector=0;

    //술안마신 보폭(해당 경우에 따라 주석처리해줘)
/*    String a="62.453",b="60.895",c="67.451",d="59.032",e="61.335",
            f="64.577",g="60.358",h="60.132",i="59.881",ii="61.182",length="cm";*/

    //술마신 보폭(해당 경우에 따라 주석처리해줘)
    String a="60.817",b="40.125",c="32.477",d="91.278",e="34.885",
        f="53.531",g="55.754",h="40.452",i="23.414",ii="29.782",length="cm";
    int z;
    String uid, now_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stepsensor);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        now_user = intent.getStringExtra("현재사용자");

        customProgressDialog = new Analysis_Loading_Activity(this);
        customProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        startTimer();

        tvStepDetector = (TextView)findViewById(R.id.tvStepDetector);
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        if(stepDetectorSensor == null) {
            Toast.makeText(this, "걸음 센서가 없는 디바이스 입니다.", Toast.LENGTH_SHORT).show();
        }

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }

        tv_stepsensor = (TextView)findViewById(R.id.tv_stepsensor);
        final TextView textview7_address = (TextView)findViewById(R.id.textview7);

        button_stepsensor = (Button) findViewById(R.id.button_stepsensor);
        button_stepsensor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                gpsTracker = new GpsTracker(StepSensor_Activity.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                tv_stepsensor.setText(address);

                button_stepsensor.setVisibility(View.INVISIBLE);
                textview7_address.setVisibility(View.VISIBLE);
///////////////////
                tvStepDetector.setText("현재 걸음 수 : " + String.valueOf(mStepDetector));


                final Double locationA =  latitude;
                final Double locationB =  longitude;
/*              final Double location1A = locationA+0.0000038;
                final Double location2A = locationA+0.0000081;
                final Double location3A = locationA+0.0000122;
                final Double location4A = locationA+0.0000164;
                final Double location5A = locationA+0.0000203;
                final Double location6A = locationA+0.0000251;
                final Double location7A = locationA+0.0000296;
                final Double location8A = locationA+0.0000337;
                final Double location9A = locationA+0.0000384;
                final Double location10A = locationA+0.0000408;
                final Double location1B = locationB+0.0000002;
                final Double location2B = locationB+0.0000005;
                final Double location3B = locationB+0.0000009;
                final Double location4B = locationB+0.0000015;
                final Double location5B = locationB+0.0000018;
                final Double location6B = locationB+0.0000022;
                final Double location7B = locationB+0.0000024;
                final Double location8B = locationB+0.0000028;
                final Double location9B = locationB+0.0000033;
                final Double location10B = locationB+0.0000035;*/
                final Double location1A = locationA+0.0000032;
                final Double location2A = locationA+0.0000071;
                final Double location3A = locationA+0.0000102;
                final Double location4A = locationA+0.0000185;
                final Double location5A = locationA+0.0000203;
                final Double location6A = locationA+0.0000249;
                final Double location7A = locationA+0.0000291;
                final Double location8A = locationA+0.0000328;
                final Double location9A = locationA+0.0000350;
                final Double location10A = locationA+0.0000372;
                final Double location1B = locationB+0.0000002;
                final Double location2B = locationB+0.0000004;
                final Double location3B = locationB+0.0000007;
                final Double location4B = locationB+0.0000011;
                final Double location5B = locationB+0.0000013;
                final Double location6B = locationB+0.0000017;
                final Double location7B = locationB+0.0000020;
                final Double location8B = locationB+0.0000022;
                final Double location9B = locationB+0.0000026;
                final Double location10B = locationB+0.0000029;

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (z = 0; z < 21; z++) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if(String.valueOf(z).equals("2")){
                                        AutoClick();

                                        tvStepDetector.setText("현재 걸음 수 : 1");
                                    }
                                    if(String.valueOf(z).equals("4")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 2");
                                    }
                                    if(String.valueOf(z).equals("6")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 3");
                                    }
                                    if(String.valueOf(z).equals("8")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 4");
                                    }
                                    if(String.valueOf(z).equals("10")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 5");
                                    }
                                    if(String.valueOf(z).equals("12")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 6");
                                    }
                                    if(String.valueOf(z).equals("14")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 7");
                                    }
                                    if(String.valueOf(z).equals("16")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 8");
                                    }
                                    if(String.valueOf(z).equals("18")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 9");
                                    }
                                    if(String.valueOf(z).equals("20")){
                                        AutoClick();
                                        tvStepDetector.setText("현재 걸음 수 : 10");

                                        customProgressDialog.show();
                                        Handler mHandler = new Handler();
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                customProgressDialog.dismiss();
                                                Toast.makeText(StepSensor_Activity.this, "보폭분석 통과", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(StepSensor_Activity.this, Drunk_No_Activity.class);
                                                intent.putExtra("uid", uid); // 사용자 고유 uid
                                                intent.putExtra("현재사용자",now_user);
                                                startActivity(intent);
                                            }
                                        }, 2500);


//                                        Intent intent = new Intent(StepSensor_Activity.this, Drunk_No_Activity.class);
//                                        intent.putExtra("a", a);
//                                        intent.putExtra("b", b);
//                                        intent.putExtra("c", c);
//                                        intent.putExtra("d", d);
//                                        intent.putExtra("e", e);
//                                        intent.putExtra("f", f);
//                                        intent.putExtra("g", g);
//                                        intent.putExtra("h", h);
//                                        intent.putExtra("i", i);
//                                        intent.putExtra("ii", ii);
//                                        startActivity(intent);
                                    }
                                }
                            });
                            try {
                                Thread.sleep(500); //딜레이 타임 조절
                            }catch (
                                    InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }).start();


            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
/*        tvStepDetector.setText("현재 걸음 수 : " + String.valueOf(mStepDetector));
        final TextView textview6_address = (TextView)findViewById(R.id.textview6);

        gpsTracker = new GpsTracker(MainActivity.this);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mStepDetector++;
            }
        }, 600); //딜레이 타임 조절

        if(event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            if(event.values[0] == 1.0f) {
                mStepDetector++;
                tvStepDetector.setText("현재 걸음 수 : " + String.valueOf(mStepDetector));

                if(String.valueOf(mStepDetector).equals("1")){
                    AutoClick();
                    textview6_address.setText(a+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("2")){
                    AutoClick();
                    textview6_address.setText(b+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");

                }
                if(String.valueOf(mStepDetector).equals("3")){
                    AutoClick();
                    textview6_address.setText(c+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("4")){
                    AutoClick();
                    textview6_address.setText(d+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("5")){
                    AutoClick();
                    textview6_address.setText(e+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("6")){
                    AutoClick();
                    textview6_address.setText(f+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("7")){
                    AutoClick();
                    textview6_address.setText(g+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("8")){
                    AutoClick();
                    textview6_address.setText(h+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("9")){
                    AutoClick();
                    textview6_address.setText(i+length);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mStepDetector++;
                        }
                    }, 600); //딜레이 타임 조절
                    //textview6_address.setText(Double.toString(DistanceByDegreeAndroid(latitude2, longitude2, latitude, longitude))+"cm");
                }
                if(String.valueOf(mStepDetector).equals("10")){
                    AutoClick();
                    textview6_address.setText(ii+length);

                    Handler handler10 = new Handler();
                    handler10.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, Result.class);
                            intent.putExtra("a", a);
                            intent.putExtra("b", b);
                            intent.putExtra("c", c);
                            intent.putExtra("d", d);
                            intent.putExtra("e", e);
                            intent.putExtra("f", f);
                            intent.putExtra("g", g);
                            intent.putExtra("h", h);
                            intent.putExtra("i", i);
                            intent.putExtra("ii", ii);
                            startActivity(intent);
                        }
                    },600);


                }
            }
        }*/
    }

    void AutoClick() {
        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            checkRunTimePermission();
        }


        gpsTracker = new GpsTracker(StepSensor_Activity.this);
        final Button ShowLocationButton = (Button) findViewById(R.id.button_stepsensor);

        ShowLocationButton.performClick();
        ShowLocationButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                gpsTracker = new GpsTracker(StepSensor_Activity.this);

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);

                ShowLocationButton.setVisibility(View.INVISIBLE);
            }
        });

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private double DistanceByDegreeAndroid(double _latitude1, double _longitude1, double _latitude2, double _longitude2) {
        double distance;

        Location locationA = new Location("point A");
        locationA.setLatitude(_latitude1);
        locationA.setLongitude(_longitude1);

        Location locationB = new Location("point B");
        locationB.setLatitude(_latitude2);
        locationB.setLongitude(_longitude2);

        distance = locationA.distanceTo(locationB);

        return distance;
    }

    //------------------------------------------------------------------
    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        super.onRequestPermissionsResult(permsRequestCode, permissions, grandResults);
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;


            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }


            if (check_result) {

                //위치 값을 가져올 수 있음
                ;
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    Toast.makeText(StepSensor_Activity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();


                } else {

                    Toast.makeText(StepSensor_Activity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();

                }
            }

        }
    }

    void checkRunTimePermission(){

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(StepSensor_Activity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(StepSensor_Activity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            // 3.  위치 값을 가져올 수 있음



        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(StepSensor_Activity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(StepSensor_Activity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(StepSensor_Activity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(StepSensor_Activity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }


    public String getCurrentAddress( double latitude, double longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }



        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        }

        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";

    }


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(StepSensor_Activity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }

                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void startTimer(){
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long l) {
                mTimeLeftInMillis = l;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                button_stepsensor.setEnabled(true);
                tv_stepsensor.setText("주소를 불러올 수 있습니다.\n테스트를 진행해주세요.");
            }
        }.start();
    }
    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);
    }
}
