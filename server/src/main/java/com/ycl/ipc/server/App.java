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
            public void addBinderStub(String serverName, IBinder binder) {
                ServiceCache.addService(serverName, binder);
            }

            @Override
            public IBinder getBinderProxy(String serverName) {
                return null;
            }

            @Override
            public IBinder getBinderStub(String serverName) {
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
