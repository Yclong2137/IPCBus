package com.ycl.ipc.server;

import android.app.Application;
import android.content.Context;
import android.os.IBinder;

import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IServerCache;

public class App extends Application {


    public static App instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        IPCBus.initialize(new IServerCache() {

            @Override
            public IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
                return null;
            }

        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

    }


}
