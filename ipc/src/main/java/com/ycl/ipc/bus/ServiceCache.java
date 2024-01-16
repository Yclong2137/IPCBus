package com.ycl.ipc.bus;

import android.os.IBinder;
import android.util.ArrayMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ServiceCache {

    private static final Map<String, IBinder> sCache = new ArrayMap<>(5);

    private static final Set<IBinder> binderSet = new HashSet<>();


    public static void addService(String name, IBinder service) {
        sCache.put(name, service);
        binderSet.add(service);
    }


    public static IBinder getService(String name) {
        return sCache.get(name);
    }

    public static IBinder getServiceByServer(Object server) {
        for (IBinder binder : binderSet) {
            if (binder instanceof TransformBinder && server == ((TransformBinder) binder).getServer()) {
                return binder;
            }
        }
        return null;
    }

}
