package com.ycl.sdk_base;


import com.ycl.ipc.annotation.IPCInterface;
import com.ycl.ipc.annotation.Oneway;

@IPCInterface
public interface ICarListener {
    /**
     * 这是测试方法
     *
     * @param a 啊打发的
     */
    @Oneway
    void test(int a);


}
