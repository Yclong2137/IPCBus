package com.ycl.ipc.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class MyService extends Service {

    private static final String TAG = "ws06_MyService";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate() called");
//        View view = LayoutInflater.from(getApplication()).inflate(R.layout.activity_main, null);
//        GradientDrawable dr = new GradientDrawable();
//        dr.setCornerRadius(24);
//        dr.setColor(Color.RED);
//        view.setBackground(dr);
//        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
//        } else {
//            lp.type = WindowManager.LayoutParams.TYPE_PHONE;
//        }
//        lp.gravity = Gravity.CENTER;
//        lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
//        lp.height=WindowManager.LayoutParams.WRAP_CONTENT;
//        lp.width=WindowManager.LayoutParams.WRAP_CONTENT;
//        windowManager.addView(view, lp);
    }
}