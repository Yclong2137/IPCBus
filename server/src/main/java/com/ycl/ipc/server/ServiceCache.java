package com.ycl.ipc.server;

import android.os.IBinder;
import android.util.ArrayMap;

import java.util.Map;


public class ServiceCache {

    private static final Map<String, IBinder> sCache = new ArrayMap<>(5);

    public static void addService(String name, IBinder service) {
        sCache.put(name, service);
    }


    public static IBinder getService(String name) {
        return sCache.get(name);
    }

}
