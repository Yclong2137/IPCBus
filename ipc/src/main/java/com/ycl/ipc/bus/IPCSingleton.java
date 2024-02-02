package com.ycl.ipc.bus;

import android.os.IBinder;

import androidx.annotation.Nullable;

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

    public T get(@Nullable IBinder delegate) {
        if (instance == null) {
            synchronized (this) {
                if (instance == null) {
                    instance = IPCBus.queryAndCreateBinderProxyInstance(ipcClass, delegate);
                }
            }
        }
        return instance;
    }


    public T get() {
        return get(null);
    }

}
