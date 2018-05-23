package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ManagerActivity extends Activity{

    String cookie;
    ListView lv;

    ArrayList<User> userList = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager);
        lv = (ListView) findViewById(R.id.manager_list);
        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        new JsonTask(new JsonTask.CallBack() {
            @Override
            public void getJsonArray(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject job = jsonArray.getJSONObject(i);
                        User user = new User(job.getString("uid"), job.getString("name"), job.getString("type"), job.getString("allowdate"), job.getString("addtime"));
                        userList.add(user);
                        UserAdapter adapter = new UserAdapter(ManagerActivity.this, R.layout.record_item, userList);
                        lv.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, cookie).execute("http://123.207.118.77/face/user/getVisitor");

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user1 = userList.get(position);
                String uid = user1.getUid();
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url("http://123.207.118.77/face/user/delVisitor?uid="+uid)
                        .addHeader("cookie", cookie)
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }



}
