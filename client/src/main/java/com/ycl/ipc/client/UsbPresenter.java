package com.ycl.ipc.client;

import androidx.annotation.NonNull;

import com.ycl.mvp.BaseMvpPresenter;

/**
 * ScanPresenter
 * Created by Yclong on 2024/4/15.
 **/
public class UsbPresenter extends BaseMvpPresenter<UsbContract.View> implements UsbContract.Presenter {

    public UsbPresenter(@NonNull UsbContract.View view) {
        super(view);
    }

    @Override
    public void scan() {
        System.out.println(".scan");
        getView().onScanSuccess();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getView().onScanSuccess();
            }
        }).start();
    }
}
