package com.ycl.ipc.bus;

import android.os.IInterface;
import android.os.RemoteCallbackList;

import androidx.annotation.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import timber.log.Timber;

/**
 * {@link RemoteCallbackList}
 *
 * @param <T>
 * @author Yclong
 */
public class RemoteCallbackListExt<T> extends RemoteCallbackList<IInterface> {

    private final Lock lock = new ReentrantLock();

    private final T mCallback;

    public RemoteCallbackListExt(@NonNull Class<T> interfaceClass) {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException(interfaceClass + " not an interface, please check.");
        }
        mCallback = (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                callMethod(method, args);
                return null;
            }
        });

    }

    private void callMethod(Method method, Object[] args) throws Exception {
        if (method == null) return;
        lock.lock();
        try {
            int count = super.beginBroadcast();
            Timber.i("%s@%s() called with COUNT = %s, args= %s", method.getDeclaringClass().getSimpleName(), method.getName(), count, Arrays.toString(args));
            for (int index = 0; index < count; index++) {
                IInterface item = super.getBroadcastItem(index);
                if (item != null) {
                    method.invoke(item, args);
                }
            }
        } catch (Exception e) {
            Timber.e("%s@%s() called with args= %s occur error.", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args));
        } finally {
            super.finishBroadcast();
            lock.unlock();
        }
    }

    @NonNull
    public final T getCallback() {
        return mCallback;
    }

    public final boolean register(@NonNull T callback) {
        Timber.i("register() called with: callback = [" + callback + "]");
        if (callback instanceof IInterface) {
            return super.register((IInterface) callback);
        }
        return false;
    }

    public final boolean unregister(@NonNull T callback) {
        Timber.i("unregister() called with: callback = [" + callback + "]");
        if (callback instanceof IInterface) {
            return super.unregister((IInterface) callback);
        }
        return false;
    }


    public final T getOwnerBroadcastItem(int index) {
        return (T) super.getBroadcastItem(index);
    }
}
