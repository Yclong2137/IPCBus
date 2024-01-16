package com.ycl.ipc.bus;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * @author Yclong
 */
public final class TransformBinder extends Binder {

    private final ServerInterface serverInterface;
    private final Object server;

    public TransformBinder(ServerInterface serverInterface, Object server) {
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
            method.handleTransact(server, data, reply);
            return true;
        }
        return super.onTransact(code, data, reply, flags);
    }

    Object getServer() {
        return server;
    }
}
