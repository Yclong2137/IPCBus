package com.ycl.ipc.client;


import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.ycl.ipc.bus.IPCSingleton;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

public class MainActivity extends Activity {

    IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);
    private static final String TAG = ">>>>>>>>>>";

    private ICarListener iCarListener = new ICarListener() {
        @Override
        public void test(int a) {
            Log.i(TAG, "test() called with: a = [" + a + "]");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IActivityManager iActivityManager = singleton.get();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (iActivityManager != null) {
                    VideoViewAngleData data = new VideoViewAngleData();
                    data.id = 1;
                    data.retCode = 2;
                    data.videoSupervisionAVMView = 3;
                    data.videoSupervisionAVMViewFRes = 4;
                    data.videoSupervisionAVMViewRes = 5;
                    String packageName = iActivityManager.getPackageName(99, data);
                    System.out.println(">>>>>>>>>>>>> " + packageName);
                }
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.register(iCarListener);
            }
        });

        findViewById(R.id.btn_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.unregister(iCarListener);
            }
        });
    }
}