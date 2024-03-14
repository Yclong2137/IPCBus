package com.ycl.ipc.bus;

import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ycl.ipc.Util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import timber.log.Timber;

/**
 * IPC Proxy
 *
 * @author Yclong
 */
public final class IPCInvocationBridge implements InvocationHandler {

    private final ServerInterface serverInterface;
    private volatile IBinder binder;

    IPCInvocationBridge(@NonNull ServerInterface serverInterface, @Nullable IBinder binder) {
        this.serverInterface = serverInterface;
        this.binder = binder;
        linkBinderDied(binder);
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        //unbox fail
        Object result = Util.defaultValue(method.getReturnType());
        try {
            if (method.getDeclaringClass() == Object.class) {
                return method.invoke(this, args);
            }

            //兼容IInterface
            if ("asBinder".equals(method.getName())) {
                return binder;
            }
            if (!isBinderAlive(binder)) {
                synchronized (serverInterface.getInterfaceClass()) {
                    if (!isBinderAlive(binder)) {
                        binder = IPCBus.queryBinderProxy(Util.getServerName(serverInterface.getInterfaceClass()));
                    }
                }
            }

            if (binder == null) {
                throw new IllegalStateException("Can not found the binder : " + serverInterface.getInterfaceClass().getSimpleName() + "@" + method.getName());
            }

            IPCMethod ipcMethod = serverInterface.getIPCMethod(method);

            if (ipcMethod == null) {
                throw new IllegalStateException("Can not found the ipc method : " + method.getDeclaringClass().getName() + "@" + method.getName());
            }
            return ipcMethod.transact(binder, args);
        } catch (Exception e) {
            Timber.e(e, "[err] %s@%s(%s) called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(method.getParameterTypes()), Arrays.toString(args));
        }
        return result;
    }

    private boolean isBinderAlive(IBinder binder) {
        return binder != null && binder.isBinderAlive();
    }

    private void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                Timber.i("binderDied binder = %s", binder);
                if (binder != null) binder.unlinkToDeath(this, 0);
            }
        };
        try {
            if (binder != null) binder.linkToDeath(deathRecipient, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
