package com.ycl.ipc.client;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.ycl.ipc.bus.IPCSingleton;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

public class MainActivity extends Activity {

    IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);
    private static final String TAG = ">>>>>>>>>>";
    IActivityManager iActivityManager;
    private ICarListener iCarListener = new ICarListener() {
        @Override
        public void test(int a) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         iActivityManager = singleton.get();
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((iActivityManager = singleton.get()) != null) {
                    VideoViewAngleData data = new VideoViewAngleData();
                    data.id = 1;
                    data.retCode = 2;
                    data.videoSupervisionAVMView = 3;
                    data.videoSupervisionAVMViewFRes = 4;
                    data.videoSupervisionAVMViewRes = 5;
                    String packageName = iActivityManager.getPackageName(99, data);
                }
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.register(new ICarListener() {
                    @Override
                    public void test(int a) {

                    }
                });
                //iActivityManager.register("iCarListener");
            }
        });

        findViewById(R.id.btn_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.unregister(iCarListener);
            }
        });

        findViewById(R.id.btn_setACState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.setACState(88888);

            }
        });

        findViewById(R.id.btn_getACState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.getACState();
            }
        });
    }
}