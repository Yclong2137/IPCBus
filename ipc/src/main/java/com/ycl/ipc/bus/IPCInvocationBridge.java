package com.ycl.ipc.bus;

import android.os.IBinder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public final class IPCInvocationBridge implements InvocationHandler {

    private final ServerInterface serverInterface;
    private final IBinder binder;

    public IPCInvocationBridge(ServerInterface serverInterface, IBinder binder) {
        this.serverInterface = serverInterface;
        this.binder = binder;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        //兼容IInterface
        if ("asBinder".equals(method.getName())) {
            return binder;
        }
        IPCMethod ipcMethod = serverInterface.getIPCMethod(method);
        if (ipcMethod == null) {
            throw new IllegalStateException("Can not found the ipc method : " + method.getDeclaringClass().getName() + "@" + method.getName());
        }
        return ipcMethod.callRemote(binder, args);
    }
}
