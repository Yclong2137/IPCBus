package com.ycl.ipc.bus;


import android.os.IBinder;
import android.os.IInterface;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * IPC总线
 */
public final class IPCBus {

    private static IServerCache sCache;

    private static final Set<TransformBinder> binderSet = new HashSet<>();

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
        binderSet.add(binder);
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

    static IBinder getBinder(Object server) {
        for (TransformBinder transformBinder : binderSet) {
            if (server == transformBinder.getServer()) {
                return transformBinder;
            }
        }
        return null;
    }

}
