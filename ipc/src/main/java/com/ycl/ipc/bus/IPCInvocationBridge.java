package com.ycl.ipc.bus;

import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * IPC Proxy
 *
 * @author Yclong
 */
public final class IPCInvocationBridge implements InvocationHandler {

    private final ServerInterface serverInterface;
    private IBinder binder;

    IPCInvocationBridge(@NonNull ServerInterface serverInterface, @Nullable IBinder binder) {
        this.serverInterface = serverInterface;
        this.binder = binder;
        linkBinderDied(binder);
    }


    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        if (binder == null || !binder.isBinderAlive()) {
            binder = IPCBus.queryBinderProxy(serverInterface.getInterfaceClass(), serverInterface.getInterfaceName());
        }

        //兼容IInterface
        if ("asBinder".equals(method.getName())) {
            return binder;
        }

        if (binder == null) {
            throw new IllegalStateException("Can not found the binder : " + method.getDeclaringClass().getName() + "@" + method.getName());
        }

        IPCMethod ipcMethod = serverInterface.getIPCMethod(method);
        if (ipcMethod == null) {
            throw new IllegalStateException("Can not found the ipc method : " + method.getDeclaringClass().getName() + "@" + method.getName());
        }
        return ipcMethod.callRemote(binder, args);
    }

    private void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                System.out.println("IPCBus binderDied >>>>>>>>>>> binder = " + binder);
                if (binder != null) binder.unlinkToDeath(this, 0);
                IPCInvocationBridge.this.binder = null;
            }
        };
        try {
            if (binder != null) binder.linkToDeath(deathRecipient, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
