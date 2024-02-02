package com.ycl.ipc.client;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import androidx.core.app.BundleCompat;

import com.ycl.ipc.client.ipc.ProviderCall;

public class ServiceManagerNative {

    private static final String TAG = ServiceManagerNative.class.getSimpleName();

    public static String SERVICE_CP_AUTH = "virtual.service.BinderProvider";



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



    private static void linkBinderDied(final IBinder binder) {
        IBinder.DeathRecipient deathRecipient = new IBinder.DeathRecipient() {
            @Override
            public void binderDied() {
                binder.unlinkToDeath(this, 0);
            }
        };
        try {
            binder.linkToDeath(deathRecipient, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }




}
