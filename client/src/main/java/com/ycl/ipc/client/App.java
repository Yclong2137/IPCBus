package com.ycl.ipc.client;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.IBinder;

import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IServerCache;

import java.util.List;

public class App extends Application {


    public static App instance;


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        IPCBus.initialize(new IServerCache() {


            @Override
            public IBinder queryBinderProxy(String serverName) {
                return ServiceManagerNative.getService(serverName);
            }


        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (getPackageName().equals(getProcessName(this))) {

        }
    }

    public static App getInstance() {
        return instance;
    }

    public static String getProcessName(Context cxt) {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }

}
