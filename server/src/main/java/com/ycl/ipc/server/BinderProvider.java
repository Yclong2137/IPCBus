package com.ycl.ipc.server;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.BundleCompat;

import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.IServerCache;
import com.ycl.ipc.bus.RemoteCallbackListExt;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;


public final class BinderProvider extends ContentProvider {

    private static final String TAG = "ws06_BinderProvider";


    private final RemoteCallbackListExt<ICarListener> callbackList = new RemoteCallbackListExt<>(ICarListener.class);

    @Override
    public boolean onCreate() {
        IPCBus.register(IActivityManager.class, new IActivityManager() {
            @Override
            public String getPackageName(int a, VideoViewAngleData data) {
                callbackList.getCallback().test(3333);
                return "这是测试包名";
            }

            @Override
            public int setACState(int state) {
                return 1000;
            }

            @Override
            public int getACState() {
                return 2000;
            }

            @Override
            public void register(ICarListener iCarListener) {
                callbackList.register(iCarListener);
            }

            @Override
            public void register(String iCarListener) {
            }

            @Override
            public void unregister(ICarListener iCarListener) {
                callbackList.unregister(iCarListener);
            }
        });
        return true;
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.i(TAG, "call() called with: method = [" + method + "], arg = [" + arg + "], extras = [" + extras + "]");
        if ("@".equals(method)) {
            Bundle bundle = new Bundle();
            BundleCompat.putBinder(bundle, "_VA_|_binder_", IPCBus.getBinder(IServerCache.IServiceFetcher.class));
            return bundle;
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

}
