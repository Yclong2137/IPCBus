package com.ycl.ipc.bus;


import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Proxy;

/**
 * IPC总线
 */
public class IPCBus {

    private static IServerCache sCache;

    public static void initialize(IServerCache cache) {
        System.out.println("IPCBus.initialize " + cache);
        sCache = cache;
    }

    private static void checkInitialized() {
        if (sCache == null) {
            throw new IllegalStateException("please call initialize() at first.");
        }
    }

    public static void register(Class<?> interfaceClass, Object server) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder binder = new TransformBinder(serverInterface, server);
        sCache.addBinderStub(serverInterface.getInterfaceName(), binder);
    }

    public static <T> T getBinderProxy(Class<?> interfaceClass, IBinder delegate) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = delegate;
        if (binder == null) {
            binder = sCache.getBinderProxy(serverInterface.getInterfaceName());
        }
        if (binder == null) {
            return null;
        }
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, IInterface.class}, new IPCInvocationBridge(serverInterface, binder));
    }

    public static <T> T getBinderProxy(Class<?> interfaceClass) {
        return getBinderProxy(interfaceClass, null);
    }


    public static IBinder getBinderStub(Class<?> interfaceClass) {
        return getBinderStub(interfaceClass.getName());

    }

    public static IBinder getBinderStub(String name) {
        return sCache.getBinderStub(name);

    }

}
