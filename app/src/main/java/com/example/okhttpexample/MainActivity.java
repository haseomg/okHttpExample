package com.example.okhttpexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Button logInBtn;
    EditText input_id, input_pwd;
    TextView signup_text;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI 요소 연결
        logInBtn = findViewById(R.id.signInBtn);
        input_id = findViewById(R.id.input_id);
        input_pwd = findViewById(R.id.input_pwd);
        signup_text = findViewById(R.id.signup_text);
        textView = findViewById(R.id.textView);

        signup_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityC(SignUp.class);
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = NetworkStatus.getConnectivityStatus(getApplicationContext());
                if (status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {

                    // EditText값 예외처리
                    if (input_id.getText().toString().trim().length() > 0 ||
                            input_pwd.getText().toString().trim().length() > 0) {

                        // 프로그래스바 보이게 처리
//                        findViewById(R.id.cpb).setVisibility(View.VISIBLE);

                        // get방식 파라미터 추가
                        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://43.201.105.106/logIn.php").newBuilder();
                        urlBuilder.addQueryParameter("v", "1.0"); // 예시
                        String url = urlBuilder.build().toString();

                        // POST 파라미터 추가
                        RequestBody formBody = new FormBody.Builder()
                                .add("id", input_id.getText().toString().trim())
                                .add("pw", input_pwd.getText().toString().trim())
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
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {

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
                                                Log.i("tag", "응답 실패");
                                                Toast.makeText(getApplicationContext(), "네트워크 문제 발생", Toast.LENGTH_SHORT).show();

                                            } else {
                                                // 응답 성공
                                                Log.i("tag", "응답 성공");
                                                final String responseData = response.body().string().trim();
                                                Log.i("tag", responseData);
                                                if (responseData.equals("1")) {
                                                    Log.i("[Main]","responseData 가 1일 때 : " + responseData);
                                                    Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Log.i("[Main]","responseData 가 1이 아닐 때 : " + responseData);
                                                    startActivityString(MainActivity.class, "nickname", responseData);
                                                    if (!responseData.equals(0)) {
                                                        textView.setText(responseData);
                                                    }
                                                }
                                            }

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }

                        });
                    } else {
                        Toast.makeText(getApplicationContext(), "입력 안된 칸이 있습니다.", Toast.LENGTH_SHORT).show();
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

    // 문자열 인텐트 전달 함수
    public void startActivityString(Class c, String name, String sendString) {
        Intent intent = new Intent(getApplicationContext(), c);
        intent.putExtra(name, sendString);
        startActivity(intent);
        // 화면전환 애니메이션 없애기
        overridePendingTransition(0, 0);
    }


}
