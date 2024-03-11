package com.ycl.ipc;

import android.os.IBinder;

import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.RemoteCallbackListExt;

/**
 * 服务拉取
 */
public interface IServiceFetcher {

    /**
     * 获取对端服务
     *
     * @param name 服务名称
     * @return 对端服务代理对象
     */
    IBinder getService(String name);

    /**
     * 注册服务状态监听器
     *
     * @param l
     */
    void registerServiceStateListener(ServiceStateListener l);

    /**
     * 反注册服务状态监听器
     *
     * @param l
     */
    void unregisterServiceStateListener(ServiceStateListener l);

    /**
     * 服务状态
     */
    interface ServiceStateListener {

        void onServiceConnected();

        void onServiceDisconnected();

    }

    class ServiceFetcher implements IServiceFetcher {
        //用于感知服务状态
        private static final RemoteCallbackListExt<ServiceStateListener> sRemoteCallbackListExt = new RemoteCallbackListExt<>(ServiceStateListener.class);

        @Override
        public IBinder getService(String name) {
            if (name != null) {
                return IPCBus.getBinder(name);
            }
            return null;
        }

        @Override
        public void registerServiceStateListener(ServiceStateListener l) {
            sRemoteCallbackListExt.register(l);
        }

        @Override
        public void unregisterServiceStateListener(ServiceStateListener l) {
            sRemoteCallbackListExt.unregister(l);
        }

        /**
         * 通知对端服务已连接
         */
        public static void notifyServiceConnected() {
            sRemoteCallbackListExt.getCallback().onServiceConnected();
        }

        /**
         * 通知对端服务断开
         */
        public static void notifyServiceDisconnected() {
            sRemoteCallbackListExt.getCallback().onServiceDisconnected();
        }

    }

}
