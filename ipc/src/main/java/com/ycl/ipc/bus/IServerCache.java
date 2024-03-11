package com.ycl.ipc.bus;

import android.os.IBinder;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.ycl.ipc.IServiceFetcher;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import timber.log.Timber;

/**
 * 服务缓存
 *
 * @author Yclong
 */
public interface IServerCache {


    void addBinder(@NonNull TransformBinder binder);

    boolean isExist(@NonNull Class<?> interfaceClass, @NonNull Object server);

    IBinder getBinder(String serverName);

    IBinder getBinder(Class<?> interfaceClass, Object server);

    void removeBinder(Class<?> interfaceClass, Object server);


    IBinder queryBinderProxy(Class<?> interfaceClass, String serverName);


    /**
     * 提供服务获取器（服务端无需提供）
     *
     * @return 对端服务代理
     */
    IBinder provideServiceFetcher();


    /**
     * 服务缓存
     */
    abstract class Cache implements IServerCache {

        private final List<TransformBinder> REGISTRY = new CopyOnWriteArrayList<>();

        /**
         * 构建服务缓存
         **/
        public Cache() {
        }

        public final void addBinder(@NonNull TransformBinder binder) {
            REGISTRY.add(binder);
        }

        public final boolean isExist(@NonNull Class<?> interfaceClass, @NonNull Object server) {
            return getBinder(interfaceClass, server) != null;
        }

        public final IBinder getBinder(String serverName) {
            for (TransformBinder binder : REGISTRY) {
                if (binder.equals(serverName)) {
                    return binder;
                }
            }
            return null;
        }

        public final IBinder getBinder(Class<?> interfaceClass, Object server) {
            for (TransformBinder binder : REGISTRY) {
                if (binder.equals(interfaceClass, server)) {
                    return binder;
                }
            }
            return null;
        }

        public final void removeBinder(Class<?> interfaceClass, Object server) {
            for (int len = REGISTRY.size(), i = len - 1; i >= 0; i--) {
                TransformBinder binder = REGISTRY.get(i);
                if (binder == null) continue;
                if (binder.equals(interfaceClass, server)) {
                    REGISTRY.remove(i);
                }
            }
        }


        public final IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
            if (IServiceFetcher.class == interfaceClass) {
                IBinder binder = provideServiceFetcher();
                linkBinderDied(binder);
                return binder;
            } else {
                return IPCBus.getServiceFetcher().getService(serverName);
            }
        }

        private void linkBinderDied(final IBinder binder) {
            IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Timber.i("binderDied binder = %s", binder);
                    REGISTRY.clear();//清除服务，防止内存泄露
                    IServiceFetcher.ServiceFetcher.notifyServiceDisconnected();
                    binder.unlinkToDeath(this, 0);
                }
            };
            try {
                if (binder != null) binder.linkToDeath(deathRecipient, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }


    }


}
