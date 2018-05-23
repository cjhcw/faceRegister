package com.arcsoft.sdk_demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RecordListActivity extends Activity {

    String cookie;
    ListView lv;

    ArrayList<User> userList = new ArrayList<User>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        lv = (ListView) findViewById(R.id.visitorList);
        Intent intent = getIntent();
        cookie = intent.getStringExtra("cookie");
        //Toast.makeText(this,cookie,Toast.LENGTH_SHORT).show();
        //requestVisitorInfo();
//        ArrayList<String> list=new ArrayList<>();
//        try{
//            if(jsonVisitors.length()!=0){
//                for(int i=0;i<jsonVisitors.length();i++){
//                    try {
//                        JSONObject job = jsonVisitors.getJSONObject(i);
//                        list.add(job.toString());
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//            else{
//                list.add(0,"无记录");
//            }
//        }catch (Exception e){
//            Log.e("json",e.getMessage());
//        }

        //Toast.makeText(this,list.toString(),Toast.LENGTH_SHORT).show();
        new JsonTask(new JsonTask.CallBack() {
            @Override
            public void getJsonArray(JSONArray jsonArray) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject job = jsonArray.getJSONObject(i);
                        User user = new User(job.getString("uid"),job.getString("name"), job.getString("type"), job.getString("allowdate"), job.getString("addtime"));
                        userList.add(user);
                        UserAdapter adapter = new UserAdapter(RecordListActivity.this, R.layout.record_item, userList);
                        lv.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        },cookie).execute("http://123.207.118.77/face/user/getVisitor");
    }
//    private void requestVisitorInfo() {
////        new Thread(new Runnable() {
////            @Override
////            public void run() {
//                OkHttpClient client = new OkHttpClient();
//                Request request = new Request.Builder()
//                        .url("http://123.207.118.77/face/user/getVisitor")
//                        .addHeader("cookie", cookie)
//                        .build();
//                Response response = null;
//                try {
//                    response = client.newCall(request).execute();
//
//                } catch (IOException e1) {
//                    e1.printStackTrace();
//                }
//                final String result;
//                try {
//                    result = response.body().string();
//                    JSONObject jsonResult = new JSONObject(result);
//                    final JSONArray jsonVisitors;
//                    if (response.isSuccessful()) {
//                        if (!TextUtils.isEmpty(result)) {
//                            Log.d("response", result);
//                            String visitors = jsonResult.getString("visitors");
//                            jsonVisitors = new JSONArray(visitors);
//                            RecordListActivity.this.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    try {
//                                        if (jsonVisitors.length() != 0) {
//                                            for (int i = 0; i < jsonVisitors.length(); i++) {
//                                                try {
//                                                    JSONObject job = jsonVisitors.getJSONObject(i);
//                                                    User user=new User(job.getString("name"),job.getString("type"),job.getString("allowdate"),job.getString("addtime"));
//                                                    userList.add(user);
//                                                } catch (JSONException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        Log.e("json", e.getMessage());
//                                    }
//                                }
//                            });
//                        }
//                    }
//                } catch (IOException e1) {
//                    Log.e("error1", e1.getMessage());
//                } catch (JSONException e) {
//                    Log.e("error2", e.getMessage());
//                }
//            }
//        }).start();
//    }
}
