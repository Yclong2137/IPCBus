package com.ycl.ipc.bus;

import android.os.Binder;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;

import com.ycl.ipc.HiExecutor;
import com.ycl.ipc.annotation.Oneway;
import com.ycl.ipc.annotation.Unsubscribe;
import com.ycl.ipc.Util;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import timber.log.Timber;

/**
 * IPC Method
 *
 * @author Yclong
 */
public final class IPCMethod {

    private final int code;
    private final Method method;
    private final String interfaceName;
    final Class<?>[] parameterTypes;
    final boolean oneway;
    final boolean unsubscribe;
    private final Converter[] converters;
    private final Converter resultConverter;


    final Converter.Factory paramConverterFactory = new Converter.Factory() {

        @Override
        public Converter get(Class<?> paramType) {
            if (isInterfaceParam(paramType)) {
                return new InterfaceParamConverter(paramType);
            }
            return null;
        }

        private boolean isInterfaceParam(Class<?> type) {
            if (type.isInterface()) {
                if (IBinder.class.isAssignableFrom(type)) {
                    return false;
                }
                if (Collection.class.isAssignableFrom(type)) {
                    return false;
                }
                if (Map.class.isAssignableFrom(type)) {
                    return false;
                }
                if (Parcelable.class.isAssignableFrom(type)) {
                    return false;
                }
                if (CharSequence.class.isAssignableFrom(type)) {
                    return false;
                }
                if (Serializable.class.isAssignableFrom(type)) {
                    return false;
                }
                return true;
            }
            return false;
        }

    };


    IPCMethod(int code, Method method, String interfaceName) {
        this.code = code;
        this.method = method;
        Class<?> returnType = method.getReturnType();
        oneway = method.isAnnotationPresent(Oneway.class) && void.class == returnType;
        unsubscribe = method.isAnnotationPresent(Unsubscribe.class);
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

    public void onTransact(Object server, Parcel data, Parcel reply) {
        Object[] args = null;
        try {
            data.enforceInterface(interfaceName);
            args = applyParamConverter(data.readArray(getClass().getClassLoader()), Converter.FLAG_ON_TRANSACT);
            if (oneway) {
                //解决防止Binder线程池过载，导致ipc无法正常通信
                HiExecutor.getInstance().execute(new AsyncTask(method, server, args));
            } else {
                Timber.i("[rcv] %s@%s called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args));
                Object res = method.invoke(server, args);
                Timber.i("[rep] %s@%s called with args = %s, result = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args), res);
                if (reply != null) {
                    reply.writeNoException();
                    reply.writeValue(res);
                }

            }
        } catch (Exception e) {
            Timber.e(e, "[err] %s@%s called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args));
            if (reply != null && !oneway) {
                reply.writeException(new IllegalStateException(e));
            }
        }
    }


    public Object transact(IBinder server, Object[] args) {
        Parcel data = Parcel.obtain();
        Parcel reply = Parcel.obtain();
        Object result = Util.defaultValue(method.getReturnType());
        boolean status;
        try {
            Timber.i("[req] %s@%s called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args));
            data.writeInterfaceToken(interfaceName);
            data.writeArray(args = applyParamConverter(args, Converter.FLAG_TRANSACT));
            if (oneway) {
                status = server.transact(code, data, null, Binder.FLAG_ONEWAY);
                handleStatus(status);
            } else {
                status = server.transact(code, data, reply, 0);
                handleStatus(status);
                reply.readException();
                result = applyResultConverter(readValue(reply), Converter.FLAG_TRANSACT);
                Timber.i("[rep] %s@%s called with args = %s, result = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args), result);
            }
        } catch (Exception e) {
            Timber.e(e, "[err] %s@%s called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(args));
        } finally {
            data.recycle();
            reply.recycle();
        }
        return result;
    }


    private void handleStatus(boolean status) {
        if (!status) {
            final String msg = "method " + method.getName() + " occur error, the transaction code(" + code + ") was not understood!!!";
            throw new IllegalStateException(msg);
        }
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


    private static class AsyncTask implements Runnable {

        private final Method method;
        private final Object server;
        private final Object[] parameters;

        public AsyncTask(Method method, Object server, Object[] parameters) {
            this.method = method;
            this.server = server;
            this.parameters = parameters;
        }

        @Override
        public void run() {
            try {
                Timber.i("[rcv] %s@%s(%s) called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(method.getParameterTypes()), Arrays.toString(parameters));
                method.invoke(server, parameters);
            } catch (Exception e) {
                Timber.e(e, "[err] %s@%s(%s) called with args = %s", method.getDeclaringClass().getSimpleName(), method.getName(), Arrays.toString(method.getParameterTypes()), Arrays.toString(parameters));
            }
        }
    }

    public interface Converter {

        int FLAG_TRANSACT = 0;
        int FLAG_ON_TRANSACT = 1;

        Object convert(Object param, int flags);

        abstract class Factory {
            public abstract Converter get(Class<?> paramType);
        }
    }


    private class InterfaceParamConverter implements Converter {

        private final Class<?> type;

        InterfaceParamConverter(Class<?> type) {
            this.type = type;
        }

        @Override
        public Object convert(Object param, int flags) {
            Object res = param;
            if (param != null) {
                switch (flags) {
                    case Converter.FLAG_ON_TRANSACT:
                        res = IPCBus.getBinderProxyInstance(type, (IBinder) param);
                        break;
                    case Converter.FLAG_TRANSACT:
                        if (!unsubscribe) {
                            IPCBus.register(type, param);
                        }
                        res = IPCBus.getBinder(type, param);
                        if (unsubscribe) {
                            if (res == null) {
                                throw new IllegalArgumentException("Can not found the binder for type = [" + type + "] server = [" + param + "]");
                            }
                            //防止内存泄漏
                            IPCBus.removeBinder(type, param);
                        }
                        break;
                }
            }
            return res;
        }
    }

}
