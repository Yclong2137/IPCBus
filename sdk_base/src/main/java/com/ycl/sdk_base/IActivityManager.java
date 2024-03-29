package com.ycl.sdk_base;

import com.ycl.ipc.annotation.IPCInterface;
import com.ycl.ipc.annotation.Oneway;
import com.ycl.ipc.annotation.Unsubscribe;
import com.ycl.sdk_base.bean.VideoViewAngleData;

@IPCInterface
public interface IActivityManager {

    String getPackageName(int a, VideoViewAngleData data);

    int setACState(int state);

    int getACState();

    @Oneway
    void register(ICarListener iCarListener);

    void register(String iCarListener);

    @Unsubscribe
    @Oneway
    default void unregister(ICarListener iCarListener) {

    }


}
