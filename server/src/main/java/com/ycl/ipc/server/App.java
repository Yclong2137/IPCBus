package com.ycl.ipc.server;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.IBinder;


import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IServerCache;
import com.ycl.ipc.bus.ServiceCache;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

import java.util.List;

public class App extends Application {


    public static App instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        IPCBus.initialize(new IServerCache() {

            @Override
            public IBinder query(String serverName) {
                return ServiceCache.getService(serverName);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }


}
