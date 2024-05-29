package com.ycl.ipc.client;

import com.ycl.mvp.IMvpView;

/**
 * ScanContract
 * Created by Yclong on 2024/4/15.
 **/
public interface UsbContract {


    interface Presenter {

        void scan();

    }


    interface View extends IMvpView<Presenter> {

        void onScanSuccess();

    }


}
