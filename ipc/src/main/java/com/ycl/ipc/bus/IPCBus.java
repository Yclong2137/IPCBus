package com.ycl.ipc.bus;


import android.os.IBinder;
import android.os.IInterface;

import com.ycl.ipc.IPCTransactHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * IPC总线
 *
 * @author Yclong
 */
public final class IPCBus {

    private static IServerCache sCache;

    private static final Set<IPCTransactHandler> ipcTransactHandlers = new HashSet<>();

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
            binder = sCache.queryBinderProxy(interfaceClass, serverInterface.getInterfaceName());
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

    static IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
        return sCache.queryBinderProxy(interfaceClass, serverName);
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

    public static void addIPCTransactHandler(IPCTransactHandler handler) {
        ipcTransactHandlers.add(handler);
    }

    public static void removeIPCTransactHandler(IPCTransactHandler handler) {
        ipcTransactHandlers.remove(handler);
    }

    static void onActionStart(Method method, Object[] args) {
        try {
            for (IPCTransactHandler ipcTransactHandler : ipcTransactHandlers) {
                if (ipcTransactHandler != null) ipcTransactHandler.onActionStart(method, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void onActionEnd(Method method, Object[] args, Object result) {
        try {
            for (IPCTransactHandler ipcTransactHandler : ipcTransactHandlers) {
                if (ipcTransactHandler != null)
                    ipcTransactHandler.onActionEnd(method, args, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
