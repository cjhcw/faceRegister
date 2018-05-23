package com.arcsoft.sdk_demo;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class JsonTask extends AsyncTask<String, Void, JSONArray> {
    CallBack callBack;

    String cookie;

    public JsonTask(CallBack callBack, String cookie) {
        this.callBack = callBack;
        this.cookie = cookie;
    }

    @Override
    protected JSONArray doInBackground(String... params) {

        JSONArray jsonArray = null;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(params[0])
                .addHeader("cookie", cookie)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String result = response.body().string();
            JSONObject jsonResult = new JSONObject(result);

            if (response.isSuccessful()) {
                if (!TextUtils.isEmpty(result)) {
                    Log.d("response", result);
                    String visitors = jsonResult.getString("visitors");
                    jsonArray = new JSONArray(visitors);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        if (callBack != null) {
            callBack.getJsonArray(jsonArray);
        }
    }

    public interface CallBack {
        void getJsonArray(JSONArray jsonArray);
    }

}
