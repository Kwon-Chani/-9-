package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class FirstActivity extends AppCompatActivity implements BeaconConsumer, TextToSpeech.OnInitListener {

    private static final String TAG = "Beacontest";
    private BeaconManager beaconManager;

    private List<Beacon> beaconList = new ArrayList<>();
    TextView textView;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    public TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        //비콘 매니저 생성,
        beaconManager = BeaconManager.getInstanceForApplication(this);
        textView = (TextView) findViewById(R.id.Textview);//비콘검색후 검색내용 뿌려주기위한 textview

        //비콘 매니저에서 layout 설정 'm:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25'
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        //beaconManager 설정 bind
        beaconManager.bind(this);

        //beacon 을 활용하려면 블루투스 권한획득(Andoird M버전 이상)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }
                });
                builder.show();
            }
        }
        tts = new TextToSpeech(this, this);
        Button zxing_barcode_scanner = (Button) findViewById(R.id.zxing_barcode_scanner);
        zxing_barcode_scanner.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), BarcodeActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
        if(tts!=null){ // 사용한 TTS객체 제거
            tts.stop();
            tts.shutdown();
        }
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            // 비콘이 감지되면 해당 함수가 호출된다. Collection<Beacon> beacons에는 감지된 비콘의 리스트가,
            // region에는 비콘들에 대응하는 Region 객체가 들어온다.
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    beaconList.clear();
                    for (Beacon beacon : beacons) {
                        beaconList.add(beacon);
                    }
                }
            }

        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {   }
    }


    // 버튼이 클릭되면 textView 에 비콘들의 정보를 뿌린다.
    public void OnButtonClicked(View view){
        // 아래에 있는 handleMessage를 부르는 함수. 맨 처음에는 0초간격이지만 한번 호출되고 나면
        // 1초마다 불러온다.
        handler.sendEmptyMessage(0);
    }
    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            textView.setText("");


            // 비콘의 아이디와 거리를 측정하여 textView에 넣는다.
            for(Beacon beacon : beaconList){
                String uuid=beacon.getId1().toString(); //beacon uuid
                int major = beacon.getId2().toInt(); //beacon major
                int minor = beacon.getId3().toInt();// beacon minor
                String address = beacon.getBluetoothAddress();
                if(minor==59683 && beacon.getDistance()<=4) {
                    //beacon 의 식별을 위하여 major값으로 확인
                    //이곳에 필요한 기능 구현
                    //textView.append("ID 1 : " + beacon.getId2() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + "m\n");
                    textView.append("비콘 1\n");
                    //tts.speak("비콘 1", TextToSpeech.QUEUE_FLUSH, null, "id1");
                    speakOut("비콘 1");
                    //textView.append("Beacon UUID : "+uuid+"\n");
                }
                else if (minor == 59681 && beacon.getDistance() <= 4) {
                    textView.append("비콘 2\n");
                    //tts.speak("비콘 2", TextToSpeech.QUEUE_FLUSH, null, "id1");
                    speakOut("비콘 2");
                }
                else if (minor == 59692 && beacon.getDistance() <= 4) {
                    textView.append("비콘 3\n");
                    //tts.speak("비콘 3", TextToSpeech.QUEUE_FLUSH, null, "id1");
                    speakOut("비콘 3");
                }
                else if (minor == 59686 && beacon.getDistance() <= 4) {
                    textView.append("비콘 4\n");
                    //tts.speak("비콘 4", TextToSpeech.QUEUE_FLUSH, null, "id1");
                    speakOut("비콘 4");
                }

                else{
                    //나머지 비콘검색
                    textView.append(beacon.getId3() + " / " + "Distance : " + Double.parseDouble(String.format("%.3f", beacon.getDistance())) + " / rssi" + beacon.getRssi() +"m\n");
                }

            }

            // 자기 자신을 1초마다 호출
            handler.sendEmptyMessageDelayed(0, 1000);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                        @Override
                        public void onDismiss(DialogInterface dialog) {
                        }

                    });
                    builder.show();
                }
                return;
            }
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speakOut(String text){
        //CharSequence text = input_text.getText();   	//바코드 숫자 여기에 입력되도록
        //CharSequence text = result.getContents();
        tts.setPitch((float)1.0); // 음성 톤 높이 지정 1.0tt
        tts.setSpeechRate((float)0.8); // 음성 속도 지정 0.8

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //             	2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    public void onInit(int status) {// OnInitListener를 통해서 TTS 초기화
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS", "This Language is not supported");
            }else{
                //speak_out.setEnabled(true);
                //speakOut();// onInit에 음성출력할 텍스트를 넣어줌
            }
        }else{
            Log.e("TTS", "Initialization Failed!");
        }

    }
}
