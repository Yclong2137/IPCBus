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

    /**
     * 初始化
     *
     * @param cache cache
     */
    public static void initialize(IServerCache cache) {
        System.out.println("IPCBus.initialize " + cache);
        sCache = cache;
    }

    private static void checkInitialized() {
        if (sCache == null) {
            throw new IllegalStateException("please call initialize() at first.");
        }
    }

    /**
     * 注册服务
     *
     * @param interfaceClass 服务接口
     * @param server         服务接口实现类
     */
    public static void register(Class<?> interfaceClass, Object server) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        TransformBinder binder = new TransformBinder(serverInterface, server);
        sCache.addBinder(binder);
    }

    /**
     * 查询并创建BinderProxy实例
     *
     * @param interfaceClass 服务接口
     * @param delegate       Binder委托
     * @param <T>
     * @return BinderProxy实例
     */
    static <T> T queryAndCreateBinderProxyInstance(Class<?> interfaceClass, IBinder delegate) {
        checkInitialized();
        ServerInterface serverInterface = new ServerInterface(interfaceClass);
        IBinder binder = delegate;
        if (binder == null) {
            binder = sCache.queryBinderProxy(interfaceClass, serverInterface.getInterfaceName());
        }
        //noinspection unchecked
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, IInterface.class}, new IPCInvocationBridge(serverInterface, binder));
    }

    /**
     * 查询并创建BinderProxy实例
     *
     * @param interfaceClass 服务接口
     * @param <T>
     * @return BinderProxy实例
     */
    static <T> T queryAndCreateBinderProxyInstance(Class<?> interfaceClass) {
        return queryAndCreateBinderProxyInstance(interfaceClass, null);
    }

    /**
     * 查询对端BinderProxy
     *
     * @param interfaceClass 服务接口
     * @param serverName     服务名称
     * @return BinderProxy
     */
    static IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
        return sCache.queryBinderProxy(interfaceClass, serverName);
    }

    /**
     * 获取Binder
     *
     * @param interfaceClass 服务接口
     * @return Binder
     */
    public static IBinder getBinder(Class<?> interfaceClass) {
        return getBinder(interfaceClass.getName());

    }

    /**
     * 获取Binder
     *
     * @param name 服务名称
     * @return Binder
     */
    public static IBinder getBinder(String name) {
        return sCache.getBinder(name);

    }

    /**
     * 通过服务实例获取Binder
     *
     * @param server 服务实例
     * @return Binder
     */
    static IBinder getBinderByServer(Object server) {
        return sCache.getBinderByServer(server);
    }

    /**
     * 通过服务实例移除Binder
     *
     * @param server 服务实例
     */
    static void removeBinderByServer(Object server) {
        sCache.removeBinderByServer(server);
    }

    /**
     * 添加IPC处理器
     *
     * @param handler
     */
    public static void addIPCTransactHandler(IPCTransactHandler handler) {
        ipcTransactHandlers.add(handler);
    }

    /**
     * 移除IPC处理器
     *
     * @param handler
     */
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
