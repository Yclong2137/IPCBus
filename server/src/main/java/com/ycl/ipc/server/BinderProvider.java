package com.ycl.ipc.server;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.BundleCompat;

import com.ycl.ipc.IPCTransactHandler;
import com.ycl.ipc.bus.IPCBus;
import com.ycl.ipc.bus.RemoteCallbackListExt;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.IServiceFetcher;
import com.ycl.sdk_base.bean.VideoViewAngleData;

import java.lang.reflect.Method;
import java.util.Arrays;


public final class BinderProvider extends ContentProvider {

    private static final String TAG = "ws06_BinderProvider";

    private final ServiceFetcher mServiceFetcher = new ServiceFetcher();

    private final RemoteCallbackListExt<ICarListener> callbackList = new RemoteCallbackListExt<>(ICarListener.class);

    @Override
    public boolean onCreate() {
//        IPCBus.addIPCTransactHandler(new IPCTransactHandler() {
//            @Override
//            public void onActionStart(Method method, Object[] args) {
//                Log.i(TAG, "onActionStart() called with: method = [" + method.getName() + "], args = [" + Arrays.toString(args) + "]");
//            }
//
//            @Override
//            public void onActionEnd(Method method, Object[] args, Object result) {
//                Log.i(TAG, "onActionEnd() called with: method = [" + method.getName() + "], args = [" + Arrays.toString(args) + "], result = [" + result + "]");
//            }
//        });
        IPCBus.register(IServiceFetcher.class, mServiceFetcher);
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
            BundleCompat.putBinder(bundle, "_VA_|_binder_", IPCBus.getBinder(IServiceFetcher.class));
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

    private static class ServiceFetcher implements IServiceFetcher {
        @Override
        public IBinder getService(String name) {
            if (name != null) {
                return IPCBus.getBinder(name);
            }
            return null;
        }

    }
}
