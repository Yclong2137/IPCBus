package com.ycl.ipc.server;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import androidx.core.app.BundleCompat;

import com.ycl.ipc.IServiceFetcher;
import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.RemoteCallbackListExt;
import com.ycl.ipc.bus.ServiceCache;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

import java.util.function.Consumer;


public final class BinderProvider extends ContentProvider {

    private static final String TAG = "ws06_BinderProvider";

    private final ServiceFetcher mServiceFetcher = new ServiceFetcher();

    private final RemoteCallbackListExt<ICarListener> callbackList = new RemoteCallbackListExt<>();

    @Override
    public boolean onCreate() {
        IPCBus.register(IActivityManager.class, new IActivityManager() {
            @Override
            public String getPackageName(int a, VideoViewAngleData data) {
                Log.i(TAG, "getPackageName() called with: a = [" + a + "], data = [" + data + "]");
                return "这是测试包名";
            }

            @Override
            public void register11(ICarListener iCarListener) {
                Log.i(TAG, "register11() called with: iCarListener = [" + iCarListener + "]");
                callbackList.register(iCarListener);
                throw new IllegalStateException("+++++++++++++++++");
//                callbackList.call(new RemoteCallbackListExt.ItemCallback<ICarListener>() {
//                    @Override
//                    public void invokeItem(ICarListener item) {
//                        iCarListener.test(3333333);
//
//                    }
//
//                });
//                System.out.println(">>>>>>>> asBinder = "+iCarListener.asBinder());
//                int count = callbackList.beginBroadcast();
//                for (int i = 0; i < count; i++) {
//                    ICarListener item = callbackList.getBroadcastItem(i);
//                    item.test(22222);
//                }
//                callbackList.finishBroadcast();
            }
        });
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.i(TAG, "call() called with: method = [" + method + "], arg = [" + arg + "], extras = [" + extras + "]");
        if ("@".equals(method)) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_VA_|_binder_", mServiceFetcher);
            return bundle;
        }
        if ("register".equals(method)) {

        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private static class ServiceFetcher extends IServiceFetcher.Stub {
        @Override
        public IBinder getService(String name) throws RemoteException {
            Log.i(TAG, "getService() called with: name = [" + name + "]");
            if (name != null) {
                return ServiceCache.getService(name);
            }
            return null;
        }

    }
}
