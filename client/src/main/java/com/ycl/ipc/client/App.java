package com.ycl.ipc.client;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.IBinder;
import android.util.Log;

import com.ycl.ipc.IPCTransactHandler;
import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IServerCache;
import com.ycl.sdk_base.IServiceFetcher;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class App extends Application {


    public static App instance;

    private static final String TAG = "ws06_App";


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        IPCBus.initialize(new IServerCache() {

            @Override
            public IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
                if (IServiceFetcher.class == interfaceClass) {
                    return ServiceManagerNative.getServiceFetcherBinder();
                }
                return ServiceManagerNative.getService(serverName);
            }


        });
        IPCBus.addIPCTransactHandler(new IPCTransactHandler() {
            @Override
            public void onActionStart(Method method, Object[] args) {
                //Log.i(TAG, "onActionStart() called with: method = [" + method.getName() + "], args = [" + Arrays.toString(args) + "]");
            }

            @Override
            public void onActionEnd(Method method, Object[] args, Object result) {
                Log.i(TAG, "onActionEnd() called with: method = [" + method.getName() + "], args = [" + Arrays.toString(args) + "], result = [" + result + "]");
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
