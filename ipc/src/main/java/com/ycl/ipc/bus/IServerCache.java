package com.ycl.ipc.bus;

import android.os.IBinder;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 服务缓存
 *
 * @author Yclong
 */
public interface IServerCache {


    List<TransformBinder> sBinderRegistry = new CopyOnWriteArrayList<>();


    default void addBinder(TransformBinder binder) {
        sBinderRegistry.add(binder);
    }

    default boolean isExist(Class<?> interfaceClass, Object server) {
        return getBinder(interfaceClass, server) != null;
    }

    default IBinder getBinder(String serverName) {
        for (TransformBinder binder : sBinderRegistry) {
            if (serverName != null && serverName.equals(binder.getInterfaceName())) {
                return binder;
            }
        }
        return null;
    }

    default IBinder getBinder(Class<?> interfaceClass, Object server) {
        for (TransformBinder binder : sBinderRegistry) {
            if (binder.getInterfaceClass() == interfaceClass && binder.getServer() == server) {
                return binder;
            }
        }
        return null;
    }

    default void removeBinder(Class<?> interfaceClass, Object server) {
        for (int len = sBinderRegistry.size(), i = len - 1; i >= 0; i--) {
            TransformBinder binder = sBinderRegistry.get(i);
            if (binder == null) continue;
            if (binder.getInterfaceClass() == interfaceClass && binder.getServer() == server) {
                sBinderRegistry.remove(i);
            }
        }
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
