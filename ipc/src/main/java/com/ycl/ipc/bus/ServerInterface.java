package com.ycl.ipc.bus;

import android.os.Binder;
import android.util.SparseArray;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务接口
 *
 * @author Yclong
 */
public final class ServerInterface {

    private static final Map<Class<?>, ServerInterface> CACHE = new HashMap<>();

    private final Class<?> interfaceClass;
    private final SparseArray<IPCMethod> codeToInterfaceMethod;
    private final Map<Method, IPCMethod> methodToIPCMethodMap;


    static ServerInterface get(@NonNull Class<?> interfaceClass) {
        ServerInterface serverInterface = CACHE.get(interfaceClass);
        if (serverInterface == null) {
            serverInterface = new ServerInterface(interfaceClass);
            CACHE.put(interfaceClass, serverInterface);
        }
        return serverInterface;
    }

    private ServerInterface(@NonNull Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        Method[] methods = interfaceClass.getMethods();
        //防止顺序不一致导致code找不到正确的方法
        Arrays.sort(methods, new Comparator<Method>() {
            @Override
            public int compare(Method o1, Method o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        codeToInterfaceMethod = new SparseArray<>(methods.length);
        methodToIPCMethodMap = new HashMap<>(methods.length);
        for (int i = 0; i < methods.length; i++) {
            int code = Binder.FIRST_CALL_TRANSACTION + i;
            IPCMethod ipcMethod = new IPCMethod(code, methods[i], interfaceClass.getName());
            codeToInterfaceMethod.put(code, ipcMethod);
            methodToIPCMethodMap.put(methods[i], ipcMethod);
        }
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public String getInterfaceName() {
        return interfaceClass.getName();
    }

    public IPCMethod getIPCMethod(int code) {
        return codeToInterfaceMethod.get(code);
    }

    public IPCMethod getIPCMethod(Method method) {
        return methodToIPCMethodMap.get(method);
    }
}
