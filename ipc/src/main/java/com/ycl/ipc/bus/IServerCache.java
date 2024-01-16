package com.ycl.ipc.bus;

import android.os.IBinder;


public interface IServerCache {

    default void addBinderStub(String serverName, IBinder binder) {
        ServiceCache.addService(serverName, binder);
    }

    default IBinder getBinderStub(String serverName) {
        return ServiceCache.getService(serverName);
    }

    default IBinder getBinderStubByServer(Object server) {
        return ServiceCache.getServiceByServer(server);
    }

    IBinder queryBinderProxy(String serverName);
}
