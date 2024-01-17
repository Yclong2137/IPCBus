package com.ycl.ipc.bus;

import android.os.IBinder;

/**
 * @param <T>
 * @author Yclong
 */
public final class IPCSingleton<T> {

    private final Class<?> ipcClass;
    private T instance;

    public IPCSingleton(Class<?> ipcClass) {
        this.ipcClass = ipcClass;
    }

    public T get(IBinder delegate) {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = IPCBus.queryBinderProxyInstance(ipcClass, delegate);
                }
            }
        }
        return instance;
    }


    public T get() {
        return get(null);
    }

}
