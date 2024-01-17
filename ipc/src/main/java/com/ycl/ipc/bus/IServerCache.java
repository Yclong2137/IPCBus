package com.ycl.ipc.bus;

import android.os.IBinder;

/**
 * @author Yclong
 */
public interface IServerCache {

    default void addBinder(TransformBinder binder) {
        ServiceCache.addService(binder);
    }

    default IBinder getBinder(String serverName) {
        return ServiceCache.getService(serverName);
    }

    default IBinder getBinderByServer(Object server) {
        return ServiceCache.getServiceByServer(server);
    }

    default void removeBinderByServer(Object server) {
        ServiceCache.removeBinderByServer(server);
    }

    IBinder queryBinderProxy(String serverName);
}
