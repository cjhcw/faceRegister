package com.arcsoft.sdk_demo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends Activity implements View.OnClickListener {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    EditText Username, Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        Button Login = (Button) findViewById(R.id.Login);
        Button Cancel = (Button) findViewById(R.id.Cancel);
        Username = (EditText) findViewById(R.id.Username);
        Password = (EditText) findViewById(R.id.Password);
        Login.setOnClickListener(this);
        Cancel.setOnClickListener(this);
        ActionBar actionBar=getActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Login:
                sendRequest();
                break;
            case R.id.Cancel:
                Username.setText("");
                Password.setText("");
        }

    }

    private void sendRequest() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject();
                    OkHttpClient client = new OkHttpClient();
                    obj.put("username", Username.getText().toString());
                    obj.put("password", Password.getText().toString());
                    RequestBody requestBody = RequestBody.create(JSON, obj.toString());
                    Request request = new Request.Builder()
                            .url("http://123.207.118.77/face/login/on")
                            .post(requestBody)
                            .build();
                    Response response = client.newCall(request).execute();
                    String cookie = response.header("Set-Cookie");
                    Log.d("cookie", cookie);
                    final String result = response.body().string();
                    //判断请求是否成功
                    if (response.isSuccessful()) {
                        JSONObject json = new JSONObject(result);
                        //Intent intent = new Intent(MainActivity.this, PersonInfo_Activity.class);
                        JSONObject user = new JSONObject(json.getString("user"));
                        if (!TextUtils.isEmpty(result)) {
                            Intent intent = new Intent(LoginActivity.this, BaseActivity.class);
                            intent.putExtra("cookie", cookie);
                            intent.putExtra("name", user.getString("name"));
                            intent.putExtra("Username", user.getString("userName"));
                            intent.putExtra("UserType", user.getString("type"));
                            intent.putExtra("Address", user.getString("address"));
                            intent.putExtra("AddTime", user.getString("addtime"));
                            intent.putExtra("AllowDate", user.getString("allowdate"));
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                try {
//                    JSONObject obj = new JSONObject();
//                    obj.put("username", Username.getText().toString());
//                    obj.put("password", Password.getText().toString());
//                    OkHttpClient client = new OkHttpClient();
//                    RequestBody requestBody = RequestBody.create(JSON, obj.toString());
//                    Request request = new Request.Builder()
//                            .url("http://123.207.118.77/face/login/on")
//                            .post(requestBody)
//                            .build();
//                    Response response = client.newCall(request).execute();
//                    String cookie = response.header("Set-Cookie");
//                    Log.d("cookie", cookie);
//                    final String result = response.body().string();
//                    //判断请求是否成功
//                    if (response.isSuccessful()) {
//                        try {
//                            JSONObject json = new JSONObject(result);
//                            Intent intent = new Intent(MainActivity.this, PersonInfo_Activity.class);
//                            JSONObject user = new JSONObject(json.getString("user"));
//                            if (!TextUtils.isEmpty(result)) {
//                                intent.putExtra("Name", user.getString("name"));
//                                intent.putExtra("Username", user.getString("userName"));
//                                intent.putExtra("UserType", user.getString("type"));
//                                intent.putExtra("Address", user.getString("address"));
//                                intent.putExtra("AddTime", user.getString("addtime"));
//                                intent.putExtra("AllowDate", user.getString("allowdate"));
//                                intent.putExtra("cookie",cookie);
//                                startActivity(intent);
//                            }
//                        } catch (Exception e) {
//                            Log.e("error", "Exception = " + e);
//                        }
//                    } else {
//                        Toast.makeText(MainActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }
        }).start();

    }
}