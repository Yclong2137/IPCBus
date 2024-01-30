package com.ycl.ipc.bus;

import android.os.IInterface;
import android.os.RemoteCallbackList;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * {@link RemoteCallbackList}
 * @param <T>
 * @author Yclong
 */
public class RemoteCallbackListExt<T> {

    private final RemoteCallbackList<IInterface> remoteCallbackList = new RemoteCallbackList<>();

    private final Lock lock = new ReentrantLock();

    public boolean register(T callback) {
        if (callback instanceof IInterface) {
            return remoteCallbackList.register((IInterface) callback);
        }
        return false;
    }

    public boolean unregister(T callback) {
        if (callback instanceof IInterface) {
            return remoteCallbackList.unregister((IInterface) callback);
        }
        return false;
    }

    public void call(ItemCallback<T> itemCallback) {
        lock.lock();
        try {
            int count = remoteCallbackList.beginBroadcast();
            for (int index = 0; index < count; index++) {
                IInterface item = remoteCallbackList.getBroadcastItem(index);
                if (item != null && itemCallback != null) {
                    itemCallback.invokeItem((T) item);
                }
            }
        } finally {
            remoteCallbackList.finishBroadcast();
            lock.unlock();
        }
    }


    public interface ItemCallback<T> {

        void invokeItem(T item);

    }

}
