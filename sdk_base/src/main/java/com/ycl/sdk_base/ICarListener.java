package com.ycl.sdk_base;


import com.ycl.ipc.annotation.IPCInterface;
import com.ycl.ipc.annotation.Oneway;

@IPCInterface
public interface ICarListener {
    @Oneway
    void test(int a);


}
