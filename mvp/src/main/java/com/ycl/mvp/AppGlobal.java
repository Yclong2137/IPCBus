package com.ycl.mvp;

import android.annotation.SuppressLint;
import android.app.Application;

import java.lang.reflect.InvocationTargetException;

/**
 * AppGlobal
 * Created by Yclong on 2024/4/15.
 **/
public final class AppGlobal {

    private static Application sApplication;

    private AppGlobal() {
    }

    @SuppressLint("PrivateApi")
    public static Application getApplication() {
        if (sApplication == null) {
            try {
                sApplication = (Application) Class.forName("android.app.ActivityThread")
                        .getMethod("currentApplication")
                        .invoke(null, (Object[]) null);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return sApplication;
    }

}
