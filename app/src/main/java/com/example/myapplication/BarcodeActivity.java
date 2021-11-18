package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.Locale;

public class BarcodeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{


    EditText et;
    Button bt;
    IntentIntegrator integrator;
    //private Object TTS;
    public TextToSpeech tts;
    public Button speak_out;
    public EditText input_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        et = findViewById(R.id.et);


        bt = findViewById(R.id.bt);



        et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    //bt의 onClick을 실행
                    bt.callOnClick();
                    //키보드 숨기기
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),0);
                    return true;
                }
                return false;
            }
        });


        integrator = new IntentIntegrator(this);



        //바코드 안의 텍스트
        integrator.setPrompt("바코드를 사각형 안에 비춰주세요");

        //바코드 인식시 소리 여부
        integrator.setBeepEnabled(true);


        integrator.setBarcodeImageEnabled(true);

        integrator.setCaptureActivity(CaptureActivity.class);

        //바코드 스캐너 시작
        integrator.initiateScan();
        //tts
        tts = new TextToSpeech(this, this);
        speak_out = findViewById(R.id.button);
        input_text = findViewById(R.id.et);
        //speakOut();
        speak_out.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) // LOLLIPOP이상 버전에서만 실행 가능
            @Override
            public void onClick(View v){
                speakOut();
            }
        });
    }


    public void onClick(View view){
        String address = et.getText().toString();

        if(!address.startsWith("http://")){
            address = "http://" + address;
        }


    }

    @Override
    public void onBackPressed() {

        //스캐너 재시작
        super.onBackPressed();
        //integrator.initiateScan();
        startActivity(new Intent(this,MainActivity.class));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null){
            if(result.getContents() == null){

            }else{
                //qr코드를 읽어서 EditText에 입력해줍니다.
                et.setText(result.getContents());

                //Button의 onclick호출
                bt.callOnClick();

                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_SHORT).show();

            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
//tts
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void speakOut(){
        CharSequence text = input_text.getText();       //바코드 숫자 여기에 입력되도록
        //CharSequence text = result.getContents();
        tts.setPitch((float)1.0); // 음성 톤 높이 지정 1.0
        tts.setSpeechRate((float)0.8); // 음성 속도 지정 0.8

        // 첫 번째 매개변수: 음성 출력을 할 텍스트
        // 두 번째 매개변수: 1. TextToSpeech.QUEUE_FLUSH - 진행중인 음성 출력을 끊고 이번 TTS의 음성 출력
        //                 2. TextToSpeech.QUEUE_ADD - 진행중인 음성 출력이 끝난 후에 이번 TTS의 음성 출력
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "id1");
    }

    @Override
    public void onDestroy() {
        if(tts!=null){ // 사용한 TTS객체 제거
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) { // OnInitListener를 통해서 TTS 초기화
        if(status == TextToSpeech.SUCCESS){
            int result = tts.setLanguage(Locale.KOREA); // TTS언어 한국어로 설정

            if(result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA){
                Log.e("TTS", "This Language is not supported");
            }else{
                speak_out.setEnabled(true);
                speakOut();// onInit에 음성출력할 텍스트를 넣어줌
            }
        }else{
            Log.e("TTS", "Initialization Failed!");
        }
    }
}
