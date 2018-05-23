//package com.arcsoft.sdk_demo;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.util.AttributeSet;
//import android.view.View;
//import android.widget.TextView;
//import android.widget.Toolbar;
//
//
//public class SimpleToolbar extends Toolbar {
//
//    private TextView mTxtMiddleTitle;
//
//    public SimpleToolbar(Context context) {
//        super(context);
//    }
//
//    public SimpleToolbar(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public SimpleToolbar(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @Override
//    protected void onFinishInflate() {
//        super.onFinishInflate();
//        mTxtMiddleTitle = findViewById(R.id.txt_main_title);
//    }
//
//    //设置中间title的内容
//    public void setMainTitle(String text) {
//        this.setTitle(" ");
//        mTxtMiddleTitle.setVisibility(View.VISIBLE);
//        mTxtMiddleTitle.setText(text);
//    }
//
//    //设置中间title的内容文字的颜色
//    public void setMainTitleColor(int color) {
//        mTxtMiddleTitle.setTextColor(color);
//    }
//    public void setBackgroundColor(Color color){
//
//    }
//
//}
