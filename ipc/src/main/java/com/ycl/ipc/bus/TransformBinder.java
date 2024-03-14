package com.ycl.ipc.bus;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

import androidx.annotation.NonNull;

import com.ycl.ipc.Util;

/**
 * Binder Stub
 *
 * @author Yclong
 */
public final class TransformBinder extends Binder {

    private final ServerInterface serverInterface;
    private final Object server;

    TransformBinder(@NonNull ServerInterface serverInterface, @NonNull Object server) {
        this.serverInterface = serverInterface;
        this.server = server;
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        if (code == INTERFACE_TRANSACTION) {
            reply.writeString(serverInterface.getInterfaceName());
            return true;
        }
        IPCMethod method = serverInterface.getIPCMethod(code);
        if (method != null) {
            method.onTransact(server, data, reply);
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    final boolean equals(Class<?> interfaceClass, Object server) {
        return interfaceClass == this.serverInterface.getInterfaceClass() && server == this.server;
    }

    final boolean equals(String serverName) {
        return serverName != null && serverName.equals(Util.getServerName(serverInterface.getInterfaceClass()));
    }

}
