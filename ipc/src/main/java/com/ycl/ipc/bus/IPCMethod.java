package com.ycl.ipc.bus;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.ycl.ipc.annotation.Oneway;
import com.ycl.ipc.annotation.Unsubscribe;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


public final class IPCMethod {

    private final int code;
    private final Method method;
    private final String interfaceName;
    final Class<?>[] parameterTypes;
    final boolean oneway;
    private final Converter[] converters;
    private final Converter resultConverter;


    final Converter.Factory paramConverterFactory = new Converter.Factory() {

        @Override
        public Converter get(Class<?> paramType) {
            if (isInterfaceParam(paramType)) {
                return new InterfaceParamConverter(method, paramType);
            }
            return null;
        }

        private boolean isInterfaceParam(Class<?> type) {
            return type.isInterface();
        }

    };


    public IPCMethod(int code, Method method, String interfaceName) {
        this.code = code;
        this.method = method;
        Class<?> returnType = method.getReturnType();
        oneway = method.isAnnotationPresent(Oneway.class) && void.class == returnType;
        this.interfaceName = interfaceName;
        parameterTypes = method.getParameterTypes();
        converters = new Converter[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            converters[i] = paramConverterFactory.get(parameterTypes[i]);
        }
        resultConverter = paramConverterFactory.get(returnType);

    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public Method getMethod() {
        return method;
    }

    public void handleTransact(Object server, Parcel data, Parcel reply) {
        data.enforceInterface(interfaceName);
        Object[] parameters = data.readArray(getClass().getClassLoader());
        try {
            Object res = method.invoke(server, applyParamConverter(parameters, Converter.FLAG_ON_TRANSACT));
            if (reply != null && !oneway) {
                reply.writeNoException();
                reply.writeValue(res);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            if (reply != null && !oneway) {
                reply.writeException(new IllegalStateException(e));
            }
        }
    }


    public Object callRemote(IBinder server, Object[] args) throws RemoteException {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        Object result = null;
        boolean status;
        try {
            data.writeInterfaceToken(interfaceName);
            data.writeArray(applyParamConverter(args, Converter.FLAG_TRANSACT));
            if (oneway) {
                status = server.transact(code, data, null, Binder.FLAG_ONEWAY);
                if (handleStatus(status)) return null;
            } else {
                status = server.transact(code, data, reply, 0);
                if (handleStatus(status)) return null;
                reply.readException();
                result = applyResultConverter(readValue(reply), Converter.FLAG_TRANSACT);
            }
        } finally {
            data.recycle();
            reply.recycle();
        }
        return result;
    }

    private boolean handleStatus(boolean status) {
        if (!status) {
            final String msg = "method " + method.getName() + " occur error, the transaction code(" + code + ") was not understood!!!";
            new IllegalStateException(msg).printStackTrace();
            return true;
        }
        return false;
    }

    private Object readValue(Parcel replay) {
        Object result = replay.readValue(getClass().getClassLoader());
        if (result instanceof Parcelable[]) {
            Parcelable[] parcelables = (Parcelable[]) result;
            Object[] results = (Object[]) Array.newInstance(Objects.requireNonNull(method.getReturnType().getComponentType()), parcelables.length);
            System.arraycopy(parcelables, 0, results, 0, results.length);
            return results;
        }
        return result;
    }

    private Object[] applyParamConverter(Object[] args, int flags) {
        if (args == null || args.length == 0) return args;
        for (int i = 0; i < args.length; i++) {
            Converter converter = converters[i];
            if (converter != null) {
                args[i] = converter.convert(args[i], flags);
            }
        }
        return args;
    }

    private Object applyResultConverter(Object result, int flags) {
        if (resultConverter != null) {
            return resultConverter.convert(result, flags);
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPCMethod ipcMethod = (IPCMethod) o;

        return Objects.equals(method, ipcMethod.method);
    }


    public interface Converter {

        int FLAG_TRANSACT = 0;
        int FLAG_ON_TRANSACT = 1;

        Object convert(Object param, int flags);

        abstract class Factory {
            public abstract Converter get(Class<?> paramType);
        }
    }


    private static class InterfaceParamConverter implements Converter {

        private final Class<?> type;
        private final boolean unsubscribe;

        InterfaceParamConverter(Method method, Class<?> type) {
            this.type = type;
            this.unsubscribe = method.isAnnotationPresent(Unsubscribe.class);
        }

        @Override
        public Object convert(Object param, int flags) {
            Object res = param;
            if (param != null) {
                switch (flags) {
                    case Converter.FLAG_ON_TRANSACT:
                        res = IPCBus.getBinderProxy(type, (IBinder) param);
                        break;
                    case Converter.FLAG_TRANSACT:
                        //作为Server
                        IBinder binder = IPCBus.getBinder(param);
                        if (binder == null && !unsubscribe) {
                            IPCBus.register(type, param);
                        }
                        res = IPCBus.getBinder(param);
                        break;
                }
            }
            return res;
        }
    }

}
