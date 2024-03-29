package com.ycl.ipc.bus;


import android.os.IBinder;
import android.os.IInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ycl.ipc.BuildConfig;
import com.ycl.ipc.IServiceFetcher;
import com.ycl.ipc.Util;

import java.lang.reflect.Proxy;

import timber.log.Timber;

/**
 * IPC总线
 *
 * @author Yclong
 */
public final class IPCBus {


    private static final IPCSingleton<IServiceFetcher> serviceFetcher = new IPCSingleton<>(IServiceFetcher.class);


    private static volatile IServerCache sCache;

    /**
     * 初始化
     *
     * @param cache cache
     */
    public static void initialize(boolean server, @NonNull IServerCache cache) {
        initTimber();
        Timber.i("initialize %s", cache);
        sCache = cache;
        if (server) register(IServiceFetcher.class, new IServiceFetcher.ServiceFetcher());
    }

    private static void initTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree() {
                @Override
                protected void log(int priority, String tag, @NonNull String message, Throwable t) {
                    super.log(priority, "IPCBus->" + tag, message, t);
                }
            });
        } else {
            Timber.plant(new Timber.Tree() {
                @Override
                protected void log(int priority, @Nullable String tag, @NonNull String message, @Nullable Throwable t) {
                    if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                        return;
                    }
                    FakeCrashLibrary.log(priority, "IPCBus->" + tag, message);

                    if (t != null) {
                        if (priority == Log.ERROR) {
                            FakeCrashLibrary.logError(t);
                        } else if (priority == Log.WARN) {
                            FakeCrashLibrary.logWarning(t);
                        }
                    }
                }
            });
        }
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
    public static void register(@NonNull Class<?> interfaceClass, @NonNull Object server) {
        checkInitialized();
        Timber.i("register() called with: interfaceClass = [" + interfaceClass + "], server = [" + server + "]");
        if (sCache.isExist(interfaceClass, server)) {
            return;
        }
        ServerInterface serverInterface = ServerInterface.get(interfaceClass);
        TransformBinder binder = new TransformBinder(serverInterface, server);
        sCache.addBinder(binder);
    }

    /**
     * 查询BinderProxy实例
     *
     * @param interfaceClass 服务接口
     * @param delegate       Binder委托
     * @param <T>
     * @return BinderProxy实例
     */
    static <T> T getBinderProxyInstance(@NonNull Class<?> interfaceClass, @Nullable IBinder delegate) {
        ServerInterface serverInterface = ServerInterface.get(interfaceClass);
        // TODO: 2024/3/14 缓存处理
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass, IInterface.class}, new IPCInvocationBridge(serverInterface, delegate));
    }


    /**
     * 查询对端BinderProxy
     *
     * @param serverName 服务名称
     * @return BinderProxy
     */
    static IBinder queryBinderProxy(@NonNull String serverName) {
        checkInitialized();
        return sCache.queryBinderProxy(serverName);
    }

    /**
     * 获取Binder
     *
     * @param interfaceClass 服务接口
     * @return Binder
     */
    public static IBinder getBinder(@NonNull Class<?> interfaceClass) {
        return getBinder(Util.getServerName(interfaceClass));

    }

    /**
     * 获取Binder
     *
     * @param name 服务名称
     * @return Binder
     */
    public static IBinder getBinder(@NonNull String name) {
        checkInitialized();
        return sCache.getBinder(name);

    }

    /**
     * 通过服务实例获取Binder
     *
     * @param server 服务实例
     * @return Binder
     */
    static IBinder getBinder(@NonNull Class<?> interfaceClass, @NonNull Object server) {
        checkInitialized();
        return sCache.getBinder(interfaceClass, server);
    }

    /**
     * 通过服务实例移除Binder
     *
     * @param server 服务实例
     */
    static void removeBinder(@NonNull Class<?> interfaceClass, @NonNull Object server) {
        checkInitialized();
        sCache.removeBinder(interfaceClass, server);
    }

    /**
     * 服务拉取
     */
    static IServiceFetcher getServiceFetcher() {
        return serviceFetcher.get();
    }

    private static final class FakeCrashLibrary {
        public static void log(int priority, String tag, String message) {
            Log.println(priority, tag, message);
        }

        public static void logWarning(Throwable t) {
            // TODO report non-fatal warning.
        }

        public static void logError(Throwable t) {
            // TODO report non-fatal error.
        }

        private FakeCrashLibrary() {
            throw new AssertionError("No instances.");
        }
    }

}
