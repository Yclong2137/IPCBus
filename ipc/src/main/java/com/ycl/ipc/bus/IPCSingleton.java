package com.ycl.ipc.bus;

import android.os.IBinder;

import com.ycl.ipc.Util;

/**
 * IPCSingleton
 *
 * @param <T>
 * @author Yclong
 */
public final class IPCSingleton<T> {

    private final Class<?> ipcClass;

    private T instance;

    public IPCSingleton(Class<?> ipcClass) {
        this.ipcClass = ipcClass;
    }

    public T get() {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    IBinder binder = IPCBus.queryBinderProxy(Util.getServerName(ipcClass));
                    instance = IPCBus.getBinderProxyInstance(ipcClass, binder);
                }
            }
        }
        return instance;
    }

}
