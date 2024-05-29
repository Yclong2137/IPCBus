package com.ycl.mvp;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.OnLifecycleEvent;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * BasePresenter
 * Created by Yclong on 2024/4/12.
 **/
public abstract class BaseMvpPresenter<V extends IMvpView> implements IMvpPresenter {

    private final String TAG = getClass().getSimpleName();

    private V mView;
    private final V mProxyView;
    private static final Handler sHandler = new Handler(Looper.getMainLooper());

    public BaseMvpPresenter(@NonNull V view) {
        this.mView = view;
        this.mProxyView = createProxyView((Class<V>) view.getClass());
        view.getLifecycle().addObserver(this);
        view.setPresenter(this);
    }

    /**
     * 创建ProxyView
     */
    private V createProxyView(Class<V> clazz) {
        return (V) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Class<?> returnType = method.getReturnType();
                if (method.getDeclaringClass() == Object.class) {
                    return method.invoke(this, args);
                }
                if (isMainThread()) {
                    return mView != null ? method.invoke(mView, args) : defaultValue(returnType);
                } else {
                    sHandler.post(() -> {
                        try {
                            if (mView != null) method.invoke(mView, args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                }
                return defaultValue(returnType);
            }

            /**
             * 默认值
             * @param returnType 返回类型
             * @return bool->false
             *         Number->0
             *         Other->null
             */
            private Object defaultValue(Class<?> returnType) {
                if (returnType == void.class || returnType == Void.class) {
                    return null;
                }
                if (returnType == boolean.class || returnType == Boolean.class) {
                    return false;
                }
                if (returnType.isPrimitive() || Number.class.isAssignableFrom(returnType)) {
                    return 0;
                }
                return null;
            }

        });
    }


    @NonNull
    protected V getView() {
        return mProxyView;
    }


    protected boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    public void onCreate() {
        Log.i(TAG, "onCreate() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_START)
    public void onStart() {
        Log.i(TAG, "onStart() called");
    }


    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void onResume() {
        Log.i(TAG, "onResume() called");
    }


    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        Log.i(TAG, "onPause() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    public void onStop() {
        Log.i(TAG, "onStop() called");
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        Log.i(TAG, "onDestroy() called");
        this.mView = null;
        sHandler.removeCallbacksAndMessages(null);
    }

}
