package com.ycl.sdk_base;

import android.os.IBinder;

public interface IServiceFetcher {

    IBinder getService(String name);

}
