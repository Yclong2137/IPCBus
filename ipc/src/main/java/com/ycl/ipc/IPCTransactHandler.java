package com.ycl.ipc;

import java.lang.reflect.Method;

public interface IPCTransactHandler {


    void onActionStart(Method method, Object[] args);

    void onActionEnd(Method method, Object[] args, Object result);

}
