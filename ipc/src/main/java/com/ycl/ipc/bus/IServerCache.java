package com.ycl.ipc.bus;

import android.os.IBinder;


public interface IServerCache {

    default void join(String serverName, IBinder binder) {
        ServiceCache.addService(serverName, binder);
    }

    /**
     * client need impl
     *
     * @param serverName 服务名称
     * @return 远端代理
     */
    IBinder query(String serverName);

    default IBinder get(String serverName) {
        return ServiceCache.getService(serverName);
    }
}
