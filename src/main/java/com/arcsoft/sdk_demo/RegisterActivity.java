package com.arcsoft.sdk_demo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Bundle;
import android.os.Message;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.arcsoft.facedetection.AFD_FSDKEngine;
import com.arcsoft.facedetection.AFD_FSDKError;
import com.arcsoft.facedetection.AFD_FSDKFace;
import com.arcsoft.facedetection.AFD_FSDKVersion;
import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.image.ImageConverter;
import com.guo.android_extend.java.ExtByteArrayOutputStream;
import com.guo.android_extend.widget.ExtImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.arcsoft.sdk_demo.LoginActivity.JSON;


public class RegisterActivity extends Activity implements SurfaceHolder.Callback {
    private final String TAG = this.getClass().toString();
    private final static int MSG_CODE = 0x1000;
    private final static int MSG_EVENT_REG = 0x1001;
    private final static int MSG_EVENT_NO_FACE = 0x1002;
    private final static int MSG_EVENT_NO_FEATURE = 0x1003;
    private final static int MSG_EVENT_FD_ERROR = 0x1004;
    private final static int MSG_EVENT_FR_ERROR = 0x1005;
    private UIHandler mUIHandler;
    private String mFilePath;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Bitmap mBitmap;
    private Rect src = new Rect();
    private Rect dst = new Rect();
    private Thread view;
    private EditText mEditText;
    private ExtImageView mExtImageView;
    private AFR_FSDKFace mAFR_FSDKFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register);
        //initial data.
        if (!getIntentData(getIntent().getExtras())) {
            Log.e(TAG, "getIntentData fail!");
            this.finish();
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        mUIHandler = new UIHandler();
        mBitmap = Application.decodeImage(mFilePath);
        src.set(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        mSurfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);
        view = new Thread(new Runnable() {
            @Override
            public void run() {
                while (mSurfaceHolder == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight() * 3 / 2];
                ImageConverter convert = new ImageConverter();
                convert.initial(mBitmap.getWidth(), mBitmap.getHeight(), ImageConverter.CP_PAF_NV21);
                if (convert.convert(mBitmap, data)) {
                    Log.d(TAG, "convert ok!");
                    Log.d("databyte[]", data.toString());
                }
                convert.destroy();

                AFD_FSDKEngine engine = new AFD_FSDKEngine();
                AFD_FSDKVersion version = new AFD_FSDKVersion();
                List<AFD_FSDKFace> result = new ArrayList<AFD_FSDKFace>();
                AFD_FSDKError err = engine.AFD_FSDK_InitialFaceEngine(FaceDB.appid, FaceDB.fd_key, AFD_FSDKEngine.AFD_OPF_0_HIGHER_EXT, 16, 5);
                Log.d(TAG, "AFD_FSDK_InitialFaceEngine = " + err.getCode());
                if (err.getCode() != AFD_FSDKError.MOK) {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_FD_ERROR;
                    reg.arg2 = err.getCode();
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_GetVersion(version);
                Log.d(TAG, "AFD_FSDK_GetVersion =" + version.toString() + ", " + err.getCode());
                err = engine.AFD_FSDK_StillImageFaceDetection(data, mBitmap.getWidth(), mBitmap.getHeight(), AFD_FSDKEngine.CP_PAF_NV21, result);
                Log.d(TAG, "AFD_FSDK_StillImageFaceDetection =" + err.getCode() + "<" + result.size());
                while (mSurfaceHolder != null) {
                    Canvas canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        Paint mPaint = new Paint();
                        boolean fit_horizontal = canvas.getWidth() / (float) src.width() < canvas.getHeight() / (float) src.height() ? true : false;
                        float scale = 1.0f;
                        if (fit_horizontal) {
                            scale = canvas.getWidth() / (float) src.width();
                            dst.left = 0;
                            dst.top = (canvas.getHeight() - (int) (src.height() * scale)) / 2;
                            dst.right = dst.left + canvas.getWidth();
                            dst.bottom = dst.top + (int) (src.height() * scale);
                        } else {
                            scale = canvas.getHeight() / (float) src.height();
                            dst.left = (canvas.getWidth() - (int) (src.width() * scale)) / 2;
                            dst.top = 0;
                            dst.right = dst.left + (int) (src.width() * scale);
                            dst.bottom = dst.top + canvas.getHeight();
                        }
                        canvas.drawBitmap(mBitmap, src, dst, mPaint);
                        canvas.save();
                        canvas.scale((float) dst.width() / (float) src.width(), (float) dst.height() / (float) src.height());
                        canvas.translate(dst.left / scale, dst.top / scale);
                        for (AFD_FSDKFace face : result) {
                            mPaint.setColor(Color.RED);
                            mPaint.setStrokeWidth(10.0f);
                            mPaint.setStyle(Paint.Style.STROKE);
                            canvas.drawRect(face.getRect(), mPaint);
                        }
                        canvas.restore();
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                        break;
                    }
                }

                if (!result.isEmpty()) {
                    AFR_FSDKVersion version1 = new AFR_FSDKVersion();
                    AFR_FSDKEngine engine1 = new AFR_FSDKEngine();
                    AFR_FSDKFace result1 = new AFR_FSDKFace();
                    AFR_FSDKError error1 = engine1.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
                    Log.d("com.arcsoft", "AFR_FSDK_InitialEngine = " + error1.getCode());
                    if (error1.getCode() != AFD_FSDKError.MOK) {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_FR_ERROR;
                        reg.arg2 = error1.getCode();
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_GetVersion(version1);
                    Log.d("com.arcsoft", "FR=" + version.toString() + "," + error1.getCode()); //(210, 178 - 478, 446), degree = 1　780, 2208 - 1942, 3370
                    error1 = engine1.AFR_FSDK_ExtractFRFeature(data, mBitmap.getWidth(), mBitmap.getHeight(), AFR_FSDKEngine.CP_PAF_NV21, new Rect(result.get(0).getRect()), result.get(0).getDegree(), result1);
                    Log.d("com.arcsoft", "Face=" + result1.getFeatureData()[0] + "," + result1.getFeatureData()[1] + "," + result1.getFeatureData()[2] + "," + error1.getCode());
                    if (error1.getCode() == error1.MOK) {
                        mAFR_FSDKFace = result1.clone();
                        int width = result.get(0).getRect().width();
                        int height = result.get(0).getRect().height();
                        Bitmap face_bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                        Canvas face_canvas = new Canvas(face_bitmap);
                        face_canvas.drawBitmap(mBitmap, result.get(0).getRect(), new Rect(0, 0, width, height), null);
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_REG;
                        reg.obj = face_bitmap;
                        mUIHandler.sendMessage(reg);
                    } else {
                        Message reg = Message.obtain();
                        reg.what = MSG_CODE;
                        reg.arg1 = MSG_EVENT_NO_FEATURE;
                        mUIHandler.sendMessage(reg);
                    }
                    error1 = engine1.AFR_FSDK_UninitialEngine();
                    Log.d("com.arcsoft", "AFR_FSDK_UninitialEngine : " + error1.getCode());
                } else {
                    Message reg = Message.obtain();
                    reg.what = MSG_CODE;
                    reg.arg1 = MSG_EVENT_NO_FACE;
                    mUIHandler.sendMessage(reg);
                }
                err = engine.AFD_FSDK_UninitialFaceEngine();
                Log.d(TAG, "AFD_FSDK_UninitialFaceEngine =" + err.getCode());
            }
        });
        view.start();

    }

    /**
     * @param bundle
     * @note bundle data :
     * String imagePath
     */
    private boolean getIntentData(Bundle bundle) {
        try {
            mFilePath = bundle.getString("imagePath");
            if (mFilePath == null || mFilePath.isEmpty()) {
                return false;
            }
            Log.i(TAG, "getIntentData:" + mFilePath);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mSurfaceHolder = holder;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mSurfaceHolder = null;
        try {
            view.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static String getFid() {
        String four = "";
        four += new Date().getTime();
        four = four.substring(0, 10);
        four += "@";
        int a = (int) (Math.random() * 10);
        int b = (int) (Math.random() * 10);
        int c = (int) (Math.random() * 10);
        int d = (int) (Math.random() * 10);
        four += a + "" + b + "" + c + "" + d;
        return four;
    }


    class UIHandler extends android.os.Handler {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_CODE) {
                if (msg.arg1 == MSG_EVENT_REG) {
                    final LayoutInflater inflater = LayoutInflater.from(RegisterActivity.this);
                    View layout = inflater.inflate(R.layout.dialog_register, null);
                    mEditText = (EditText) layout.findViewById(R.id.editview);
                    mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16)});
                    mExtImageView = (ExtImageView) layout.findViewById(R.id.extimageview);
                    mExtImageView.setImageBitmap((Bitmap) msg.obj);
                    final Bitmap face = (Bitmap) msg.obj;
                    final String uid = getFid();
                    Intent intent = getIntent();
                    final String cookie = intent.getStringExtra("cookie");
                    final String address = intent.getStringExtra("address");
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("请输入注册名字")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setView(layout)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ((Application) RegisterActivity.this.getApplicationContext()).mFaceDB.addFace(mEditText.getText().toString(), mAFR_FSDKFace);
                                    //mRegisterViewAdapter.notifyDataSetChanged();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                JSONObject obj = new JSONObject();
                                                OkHttpClient client = new OkHttpClient();

                                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                                Date dd = new Date();
                                                String date = df.format(dd);
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(dd);
                                                calendar.add(Calendar.DATE, 10);
                                                String allowdate = df.format(calendar.getTime());

                                                obj.put("uid", uid);
                                                obj.put("name", mEditText.getText().toString());
                                                obj.put("username", "hhhhh");
                                                obj.put("password", "");
                                                obj.put("address", address);
                                                obj.put("type", "访客");
                                                obj.put("allowdate", allowdate);
                                                obj.put("addtime", date);

                                                RequestBody requestBody = RequestBody.create(JSON, obj.toString());
                                                Request request = new Request.Builder()
                                                        .url("http://123.207.118.77/face/user/addVisitor")
                                                        .post(requestBody)
                                                        .addHeader("cookie", cookie)
                                                        .build();

                                                Response response = client.newCall(request).execute();
                                                String result = response.body().string();
                                                //判断请求是否成功
                                                if (response.isSuccessful()) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                                OkHttpClient client1 = new OkHttpClient();
                                                JSONObject obj1 = new JSONObject();

                                                try {
                                                    obj1.put("fid", getFid());
//                                                String feature;
//
//                                                ByteArrayOutputStream bos;
//                                                bos = new ByteArrayOutputStream();
//                                                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//将bitmap放入字节数组流中
//                                                bos.flush();//将bos流缓存在内存中的数据全部输出，清空缓存
//                                                bos.close();
//
//                                                byte[] bitmapByte = bos.toByteArray();
//                                                feature = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
////                                                byte[] data = null;
//                                                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mBitmap.getWidth(), mBitmap.getHeight(), null);
//                                                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
//                                                yuv.compressToJpeg(, 80, ops);
                                                    BASE64Encoder encoder = new BASE64Encoder();
                                                    obj1.put("feature", encoder.encode(mAFR_FSDKFace.getFeatureData()));
                                                    obj1.put("times", "0");
                                                    obj1.put("uid", uid);
                                                    obj1.put("addtime", date);
                                                    RequestBody requestBody1 = RequestBody.create(JSON, obj1.toString());
                                                    Request request1 = new Request.Builder()
                                                            .url("http://123.207.118.77/face/user/addFace")
                                                            .post(requestBody1)
                                                            .addHeader("cookie", cookie)
                                                            .build();
                                                    Response response1 = client1.newCall(request1).execute();
                                                    String result1 = response1.body().string();
                                                    //判断请求是否成功
                                                    if (response.isSuccessful()) {
                                                        if (!TextUtils.isEmpty(result)) {
                                                            Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }).start();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            JSONObject obj1 = new JSONObject();
                                            OkHttpClient client1 = new OkHttpClient();
                                            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                                            Date dd = new Date();
                                            String date = df.format(dd);


                                            try {
                                                obj1.put("fid", getFid());
//                                                String feature;
//
//                                                ByteArrayOutputStream bos;
//                                                bos = new ByteArrayOutputStream();
//                                                mBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);//将bitmap放入字节数组流中
//                                                bos.flush();//将bos流缓存在内存中的数据全部输出，清空缓存
//                                                bos.close();
//
//                                                byte[] bitmapByte = bos.toByteArray();
//                                                feature = Base64.encodeToString(bitmapByte, Base64.DEFAULT);
////                                                byte[] data = null;
//                                                YuvImage yuv = new YuvImage(data, ImageFormat.NV21, mBitmap.getWidth(), mBitmap.getHeight(), null);
//                                                ExtByteArrayOutputStream ops = new ExtByteArrayOutputStream();
//                                                yuv.compressToJpeg(, 80, ops);
                                                BASE64Encoder encoder = new BASE64Encoder();
                                                obj1.put("feature", encoder.encode(mAFR_FSDKFace.getFeatureData()));
                                                obj1.put("times", "0");
                                                obj1.put("uid", uid);
                                                obj1.put("addtime", date);
                                                RequestBody requestBody = RequestBody.create(JSON, obj1.toString());
                                                Request request1 = new Request.Builder()
                                                        .url("http://123.207.118.77/face/user/addFace")
                                                        .post(requestBody)
                                                        .addHeader("cookie", cookie)
                                                        .build();
                                                Response response = client1.newCall(request1).execute();
                                                String result = response.body().string();
                                                //判断请求是否成功
                                                if (response.isSuccessful()) {
                                                    if (!TextUtils.isEmpty(result)) {
                                                        Toast.makeText(RegisterActivity.this, "register success", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }

                                        }
                                    }).start();
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                } else if (msg.arg1 == MSG_EVENT_NO_FEATURE) {
                    Toast.makeText(RegisterActivity.this, "人脸特征无法检测，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_NO_FACE) {
                    Toast.makeText(RegisterActivity.this, "没有检测到人脸，请换一张图片", Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FD_ERROR) {
                    Toast.makeText(RegisterActivity.this, "FD初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                } else if (msg.arg1 == MSG_EVENT_FR_ERROR) {
                    Toast.makeText(RegisterActivity.this, "FR初始化失败，错误码：" + msg.arg2, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

//    class Holder {
//        ExtImageView siv;
//        TextView tv;
//    }
//    class RegisterViewAdapter extends BaseAdapter implements AdapterView.OnItemClickListener {
//        Context mContext;
//        LayoutInflater mLInflater;
//
//        public RegisterViewAdapter(Context c) {
//            // TODO Auto-generated constructor stub
//            mContext = c;
//            mLInflater = LayoutInflater.from(mContext);
//        }
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.size();
//        }
//
//        @Override
//        public Object getItem(int arg0) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            Holder holder = null;
//            if (convertView != null) {
//                holder = (Holder) convertView.getTag();
//            } else {
//                convertView = mLInflater.inflate(R.layout.item_sample, null);
//                holder = new Holder();
//                holder.siv = (ExtImageView) convertView.findViewById(R.id.imageView1);
//                holder.tv = (TextView) convertView.findViewById(R.id.textView1);
//                convertView.setTag(holder);
//            }
//
//            if (!((Application) mContext.getApplicationContext()).mFaceDB.mRegister.isEmpty()) {
//                FaceDB.FaceRegist face = ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.get(position);
//                holder.tv.setText(face.mName);
//                //holder.siv.setImageResource(R.mipmap.ic_launcher);
//                convertView.setWillNotDraw(false);
//            }
//
//            return convertView;
//        }

//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Log.d("onItemClick", "onItemClick = " + position + "pos=" + mHListView.getScroll());
//            final String name = ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mName;
//            final int count = ((Application) mContext.getApplicationContext()).mFaceDB.mRegister.get(position).mFaceList.size();
//            new AlertDialog.Builder(RegisterActivity.this)
//                    .setTitle("删除注册名:" + name)
//                    .setMessage("包含:" + count + "个注册人脸特征信息")
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            ((Application) mContext.getApplicationContext()).mFaceDB.delete(name);
//                            //mRegisterViewAdapter.notifyDataSetChanged();
//                            dialog.dismiss();
//                        }
//                    })
//                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    })
//                    .show();
//        }
//    }
}
