package com.ycl.ipc.bus;

import android.os.IBinder;

/**
 * 服务缓存
 *
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

    /**
     * 查询对端服务的代理BinderProxy
     *
     * @param interfaceClass 接口类
     * @param serverName     服务名称（对应 {@linkplain Class#getName() interfaceClass的名称}）
     * @return BinderProxy对象
     */
    IBinder queryBinderProxy(Class<?> interfaceClass, String serverName);
}
