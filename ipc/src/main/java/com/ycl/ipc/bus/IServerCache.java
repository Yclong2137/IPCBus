package com.ycl.ipc.bus;

import android.os.IBinder;


public interface IServerCache {

    void addBinderStub(String serverName, IBinder binder);

    IBinder getBinderProxy(String serverName);

    IBinder getBinderStub(String serverName);
}
