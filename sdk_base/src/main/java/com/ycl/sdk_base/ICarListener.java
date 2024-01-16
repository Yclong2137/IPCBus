package com.ycl.sdk_base;

import com.ycl.ipc.annotation.Oneway;

public interface ICarListener {
    @Oneway
    void test(int a);


}
