package com.ycl.ipc.client;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.BundleCompat;

import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IPCSingleton;
import com.ycl.ipc.client.ipc.ProviderCall;
import com.ycl.sdk_base.IServiceFetcher;

public class ServiceManagerNative {

    private static final String TAG = ServiceManagerNative.class.getSimpleName();

    public static String SERVICE_CP_AUTH = "virtual.service.BinderProvider";

    private static IServiceFetcher sFetcher;

    static IPCSingleton<IServiceFetcher> singleton = new IPCSingleton<>(IServiceFetcher.class);

    public static IBinder getServiceFetcherBinder() {
        synchronized (ServiceManagerNative.class) {
            Context context = App.getInstance();
            Bundle response = new ProviderCall.Builder(context, SERVICE_CP_AUTH).methodName("@").call();
            if (response != null) {
                return BundleCompat.getBinder(response, "_VA_|_binder_");
            }
        }
        return null;
    }


    public static void clearServerFetcher() {
        sFetcher = null;
    }

    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
                sFetcher = null;
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static IBinder getService(String name) {
        IBinder binder = getServiceFetcherBinder();
        IServiceFetcher fetcher = singleton.get(binder);
        if (fetcher != null) {
            return fetcher.getService(name);
        }
        Log.e(TAG, "GetService(%s) return null.");
        return null;
    }


}
