package com.arcsoft.sdk_demo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class BaseActivity extends Activity {

    ImageButton jibenxinxi, tianjiafangke, jiluchakan, fangkeguanli;
    String cookie, name, UserName, UserType, Address, AddTime, AllowDate;
    TextView main_content;
//    SimpleToolbar simple_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        jibenxinxi = (ImageButton) findViewById(R.id.jibenxinxi);
        tianjiafangke = (ImageButton) findViewById(R.id.tianjiafangke);
        jiluchakan = (ImageButton) findViewById(R.id.jiluchakan);
        fangkeguanli = (ImageButton) findViewById(R.id.fangkeguanli);
//        main_content = (TextView) findViewById(R.id.txt_main_title);
        final Intent intent = getIntent();
        ActionBar actionBar=getActionBar();
        if(actionBar!=null){
            actionBar.hide();
        }
        cookie = intent.getStringExtra("cookie");
        name = intent.getStringExtra("name");
        UserName = intent.getStringExtra("Username");
        UserType = intent.getStringExtra("UserType");
        Address = intent.getStringExtra("Address");
        AddTime = intent.getStringExtra("AddTime");
        AllowDate = intent.getStringExtra("AllowDate");
        //setTitleAction();
        jibenxinxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(BaseActivity.this, PersonInfo_Activity.class);
                intent1.putExtra("cookie", cookie);
                intent1.putExtra("name", name);
                intent1.putExtra("Username", UserName);
                intent1.putExtra("UserType", UserType);
                intent1.putExtra("Address", Address);
                intent1.putExtra("AddTime", AddTime);
                intent1.putExtra("AllowDate", AllowDate);
                startActivity(intent1);
            }
        });
        tianjiafangke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(BaseActivity.this, PermissionAcitivity.class);
                intent2.putExtra("cookie", cookie);
                intent2.putExtra("address",Address);
                startActivity(intent2);
            }
        });
        jiluchakan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent3 = new Intent(BaseActivity.this, RecordListActivity.class);
                intent3.putExtra("cookie", cookie);
                startActivity(intent3);
            }
        });
        fangkeguanli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(BaseActivity.this, ManagerActivity.class);
                intent4.putExtra("cookie", cookie);
                startActivity(intent4);
            }
        });
    }

}
