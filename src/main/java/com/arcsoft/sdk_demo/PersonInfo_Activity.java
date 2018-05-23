package com.arcsoft.sdk_demo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class PersonInfo_Activity extends Activity {


    TextView tv_Name, tv_Username, tv_UserType, tv_Address, tv_AddTime, tv_AllowDate;
    String cookie;
    String Name, UserName, UserType, Address, AddTime, AllowDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info_activity);
        tv_Name = (TextView) findViewById(R.id.Name);
        tv_Username = (TextView) findViewById(R.id.Username);
        tv_UserType = (TextView) findViewById(R.id.UserType);
        tv_Address = (TextView) findViewById(R.id.Address);
        tv_AddTime = (TextView) findViewById(R.id.AddTime);
        tv_AllowDate = (TextView) findViewById(R.id.AllowDate);
        Intent intent = getIntent();
        Name = intent.getStringExtra("name");
        UserName = intent.getStringExtra("Username");
        UserType = intent.getStringExtra("UserType");
        Address = intent.getStringExtra("Address");
        AddTime = intent.getStringExtra("AddTime");
        AllowDate = intent.getStringExtra("AllowDate");
        tv_Name.setText(Name);
        tv_Username.setText(UserName);
        tv_UserType.setText(UserType);
        tv_Address.setText(Address);
        tv_AddTime.setText(AddTime);
        tv_AllowDate.setText(AllowDate);
        Button outLogin = (Button) findViewById(R.id.out);
        outLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outlogin();
            }
        });
        ActionBar actionBar=getActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }

    }

    private void outlogin() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://123.207.118.77/face/login/out")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    if (response.isSuccessful()) {
                        Log.d("out", "success");
                        Intent intent = new Intent(PersonInfo_Activity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void getInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                cookie = intent.getStringExtra("cookie");
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://123.207.118.77/face/user/getInfo")
                        .addHeader("cookie", cookie)
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                    String result = response.body().string();
                    Log.d("getInfo", result);
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(result);
                        JSONObject jsonUser = new JSONObject(jsonObject.getString("user"));
                        Name = jsonUser.getString("name");
                        UserName = jsonUser.getString("userName");
                        UserType = jsonUser.getString("type");
                        Address = jsonUser.getString("address");
                        AddTime = jsonUser.getString("addtime");
                        AllowDate = jsonUser.getString("allowdate");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e1) {
                    Log.d("ioexce", e1.getMessage());
                }
            }
        }).start();
    }
}
