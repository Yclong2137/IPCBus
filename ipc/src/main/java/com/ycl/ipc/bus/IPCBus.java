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
        sCache.join(serverInterface.getInterfaceName(), binder);
    }

    public static <T> T get(Class<?> interfaceClass, IBinder service) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = service;
        if (binder == null) {
            binder = sCache.query(serverInterface.getInterfaceName());
        }
        if (binder == null) {
            return null;
        }
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, IInterface.class}, new IPCInvocationBridge(serverInterface, binder));
    }

    public static <T> T get(Class<?> interfaceClass) {
        return get(interfaceClass, null);
    }


    public static Object getServiceStub(Class<?> interfaceClass) {
        return sCache.get(interfaceClass.getName());

    }

}
