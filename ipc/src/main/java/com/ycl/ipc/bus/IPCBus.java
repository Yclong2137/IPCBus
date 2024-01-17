package com.ycl.ipc.bus;


import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Proxy;

/**
 * IPC总线
 *
 * @author Yclong
 */
public final class IPCBus {

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


    public static synchronized void register(Class<?> interfaceClass, Object server) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder binder = new TransformBinder(serverInterface, server);
        sCache.addBinder(binder);
    }

    static <T> T queryBinderProxyInstance(Class<?> interfaceClass, IBinder delegate) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = delegate;
        if (binder == null) {
            binder = sCache.queryBinderProxy(serverInterface.getInterfaceName());
        }
        if (binder == null) {
            return null;
        }
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, IInterface.class}, new IPCInvocationBridge(serverInterface, binder));
    }

    static <T> T queryBinderProxyInstance(Class<?> interfaceClass) {
        return queryBinderProxyInstance(interfaceClass, null);
    }

    static IBinder queryBinderProxy(String serverName) {
        return sCache.queryBinderProxy(serverName);
    }

    public static IBinder getBinder(Class<?> interfaceClass) {
        return getBinder(interfaceClass.getName());

    }

    public static IBinder getBinder(String name) {
        return sCache.getBinder(name);

    }

    static IBinder getBinderByServer(Object server) {
        return sCache.getBinderByServer(server);
    }

    static void removeBinderByServer(Object server) {
        sCache.removeBinderByServer(server);
    }

}
