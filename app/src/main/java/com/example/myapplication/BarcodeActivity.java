package com.example.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.util.Locale;

public class BarcodeActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener, TextToSpeech.OnInitListener {

    private CaptureManager manager;
    private DecoratedBarcodeView barcodeView;
    IntentIntegrator integrator;
    //private Object TTS;
    public TextToSpeech tts;
    private boolean isFlashOn = false;// 플래시가 켜져 있는지
    private Button btFlash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);

        barcodeView = findViewById(R.id.barcodeScanner);

        manager = new CaptureManager(this, barcodeView);
        manager.initializeFromIntent(getIntent(), savedInstanceState);
        manager.decode();

        //tts
        tts = new TextToSpeech(this, this);

//        integrator = new IntentIntegrator(this);
//
//        //바코드 안의 텍스트
////        integrator.setPrompt("바코드를 사각형 안에 비춰주세요");
//
////        바코드 인식시 소리 여부
//        integrator.setBeepEnabled(true);
//
//        integrator.setOrientationLocked(false);
//        integrator.setBarcodeImageEnabled(true);
//        integrator.setCaptureActivity(BarcodeActivity.class);
//
//        //바코드 스캐너 시작
//        integrator.initiateScan();

        btFlash = findViewById(R.id.bt_flash);
        btFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    barcodeView.setTorchOff();
                } else {
                    barcodeView.setTorchOn();
                }
            }
        });

        //speakOut("바코드를 사각형 안에 비춰주세요"); //스캐너 실행 시 문구 음성출력 하고싶은데 소리가 안 나오네요
    }

    @Override
    public void onTorchOn() {

        btFlash.setText("플래시끄기");
        isFlashOn = true;
    }

    @Override
    public void onTorchOff() {
        btFlash.setText("플래시켜기");
        isFlashOn = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        manager.onPause();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        manager.onDestroy();
//    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        manager.onSaveInstanceState(outState);
    }


//    @Override//결과값이 안 불러와집니다...
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
//            } else {
//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();
//                speakOut(result.getContents());
//                startActivity(new Intent(this, BarcodeActivity.class));
//            }
//        } else {
//            super.onActivityResult(requestCode, resultCode, data);
//        }
//    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        startActivity(new Intent(this, FirstActivity.class));

    }


    //tts
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speakOut(String text) {

        tts.setPitch((float) 1.0); // 음성 톤 높이 지정 1.0
        tts.setSpeechRate((float) 0.8); // 음성 속도 지정 0.8

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    public void onDestroy() {
        if (tts != null) { // 사용한 TTS객체 제거
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener를 통해서 TTS 초기화
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

            if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                Log.e("TTS", "This Language is not supported");
            } else {

            }
        } else {
            Log.e("TTS", "Initialization Failed!");
        }
    }
}
