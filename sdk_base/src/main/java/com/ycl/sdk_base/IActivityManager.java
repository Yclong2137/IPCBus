package com.ycl.sdk_base;

import com.ycl.sdk_base.bean.VideoViewAngleData;

public interface IActivityManager {

    String getPackageName(int a, VideoViewAngleData data);

    void register11(ICarListener iCarListener);


}
