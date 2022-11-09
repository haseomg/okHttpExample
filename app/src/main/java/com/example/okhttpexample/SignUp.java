package com.example.okhttpexample;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class SignUp extends AppCompatActivity {

    Button signUpBtn;
    EditText inputId, inputPw, inputNickName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        inputId = findViewById(R.id.input_id);
        inputPw = findViewById(R.id.input_pwd);
        inputNickName = findViewById(R.id.input_nick);

        signUpBtn = findViewById(R.id.singUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // EditText값 예외처리
                    if (inputId.getText().toString().trim().length() > 0 ||
                            inputPw.getText().toString().trim().length() > 0 ||
                            inputNickName.getText().toString().trim().length() > 0) {


                        // 프로그래스바 보이게 처리
//                        findViewById(R.id.cpb).setVisibility(View.VISIBLE);

                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://43.201.105.106/signUp.php").newBuilder();
                        urlBuilder.addQueryParameter("ver", "1.0"); // 예시
                        String url = urlBuilder.build().toString();
//                        String url = httpUrl.toString();
                        Log.i("[SignUp Activity]", "String url 확인 : " + url);


                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", inputId.getText().toString().trim())
                                .add("pw", inputPw.getText().toString().trim())
                                .add("nickname", inputNickName.getText().toString().trim())
                                .build();


                        // 요청 만들기
                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(url)
                                .post(formBody)
                                .build();

                        // 응답 콜백
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                Log.i("[SignUp]","" + e);
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {

                                Log.i("[SignUp Activity]", "onResponse 메서드 작동");

                                // 서브 스레드 Ui 변경 할 경우 에러
                                // 메인스레드 Ui 설정
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            // 프로그래스바 안보이게 처리
//                                            findViewById(R.id.cpb).setVisibility(View.GONE);

                                            if (!response.isSuccessful()) {
                                                // 응답 실패
                                                Log.i("[SignUp Activity]", "응답 실패 : " + response);
                                                Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();

                                            } else {
                                                // 응답 성공
                                                Log.i("[SignUp Activity]", "응답 성공 : " + response);
                                                final String responseData = response.body().string();
                                                Log.i("[SignUp Activity]", "응답 성공 responseData : " + responseData);

                                                if (responseData.equals("[Mysql 연결 성공] 1")) {
                                                    Log.i("[SignUp Activity]", "responseData.equals(\"0\")");
                                                    Toast.makeText(getApplicationContext(), "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                                    startActivityflag(MainActivity.class);
                                                } else {
                                                    Log.i("[SignUp Activity]", "responseData.equals(\"0\") else : " + responseData);

                                                    Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        });
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "인터넷 연결을 확인해주세요.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    // 액티비티 전환 함수

    // 인텐트 액티비티 전환함수
    public void startActivityC(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 인텐트 화면전환 하는 함수
    // FLAG_ACTIVITY_CLEAR_TOP = 불러올 액티비티 위에 쌓인 액티비티 지운다.
    public void startActivityflag(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }

    // 백스택 지우고 새로 만들어 전달
    public void startActivityNewTask(Class c) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }
}