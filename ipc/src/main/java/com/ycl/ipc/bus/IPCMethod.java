package com.ycl.ipc.bus;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

import com.ycl.ipc.annotation.Oneway;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


public class IPCMethod {

    private final int code;
    private final Method method;
    private final String interfaceName;
    final Class<?>[] parameterTypes;
    final boolean oneway;
    private final MethodParamConverter[] converters;
    private final MethodParamConverter resultConverter;


    final MethodParamConverter.Factory paramConverterFactory = new MethodParamConverter.Factory() {

        @Override
        public MethodParamConverter get(Class<?> paramType) {
            if (isInterfaceParam(paramType)) {
                return new InterfaceParamConverter(paramType);
            }
            return null;
        }

        private boolean isAidlParam(Class<?> type) {
            return type.isInterface() && IInterface.class.isAssignableFrom(type);
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
        converters = new MethodParamConverter[parameterTypes.length];

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
            Object res = method.invoke(server, applyParamConverter(parameters, true));
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
        try {
            data.writeInterfaceToken(interfaceName);
            data.writeArray(applyParamConverter(args, false));
            if (oneway) {
                server.transact(code, data, null, Binder.FLAG_ONEWAY);
            } else {
                server.transact(code, data, reply, 0);
                reply.readException();
                result = applyResultConverter(readValue(reply), false);
            }
        } finally {
            data.recycle();
            reply.recycle();
        }
        return result;
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

    private Object[] applyParamConverter(Object[] args, boolean isServer) {
        if (args == null || args.length == 0) return args;
        for (int i = 0; i < args.length; i++) {
            MethodParamConverter converter = converters[i];
            if (converter != null) {
                args[i] = converter.convert(args[i], isServer);
            }
        }
        return args;
    }

    private Object applyResultConverter(Object result, boolean isServer) {
        if (resultConverter != null) {
            return resultConverter.convert(result, isServer);
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


    public interface MethodParamConverter {

        Object convert(Object param, boolean isServer);

        abstract class Factory {
            public abstract MethodParamConverter get(Class<?> paramType);
        }
    }


    private static class InterfaceParamConverter implements MethodParamConverter {


        private final Class<?> type;

        InterfaceParamConverter(Class<?> type) {
            this.type = type;
        }

        @Override
        public Object convert(Object param, boolean isServer) {
            if (param != null) {
                if (isServer) {//server
                    IPCBus.register(type, param);
                    return IPCBus.get(type, (IBinder) param);
                } else {//client
                    IPCBus.register(type, param);
                    return IPCBus.getServiceStub(type);
                }
            }
            return null;
        }
    }

}
