package com.ycl.mvp;

import android.app.Activity;
import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;

/**
 * IMvpView
 * Created by Yclong on 2024/4/12.
 **/
public interface IMvpView<P> extends LifecycleOwner {

    default Context getCtx() {
        if (this instanceof Fragment) {
            return ((Fragment) this).requireContext();
        } else if (this instanceof Activity) {
            return (Activity) this;
        }
        return AppGlobal.getApplication();
    }

    void setPresenter(P presenter);
}
