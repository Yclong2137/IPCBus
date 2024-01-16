package com.ycl.ipc.bus;


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
                    instance = IPCBus.queryBinderProxy(ipcClass);
                }
            }
        }
        return instance;
    }

}
