package com.ycl.ipc.client;


import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.ycl.file_manager.FileListActivity;
import com.ycl.ipc.bus.IPCSingleton;
import com.ycl.sdk_base.HiLog;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements UsbContract.View {

    IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);
    private static final String TAG = ">>>>>>>>>>";
    IActivityManager iActivityManager;
    private ICarListener iCarListener = new ICarListener() {
        @Override
        public void test(int a) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ImageView iv = findViewById(R.id.iv);
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    new FileScanManager().scan(getCacheDir().getPath(), null);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        startService(new Intent(this, MyService.class));
//        iActivityManager = singleton.get();
//        new UsbPresenter(this);
        findViewById(R.id.btn_file_manager).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FileListActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((iActivityManager = singleton.get()) != null) {
                    VideoViewAngleData data = new VideoViewAngleData();
                    data.id = 1;
                    data.retCode = 2;
                    data.videoSupervisionAVMView = 3;
                    data.videoSupervisionAVMViewFRes = 4;
                    data.videoSupervisionAVMViewRes = 5;
                    String packageName = iActivityManager.getPackageName(99, data);
                }
            }
        });
        findViewById(R.id.btn_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.register(iCarListener);
                //iActivityManager.register("iCarListener");
            }
        });

        findViewById(R.id.btn_unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.unregister(iCarListener);
            }
        });

        findViewById(R.id.btn_setACState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HiLog.i(TAG, "onClick", 1);
                iActivityManager.setACState(88888);

            }
        });

        findViewById(R.id.btn_getACState).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iActivityManager.getACState();
            }
        });

    }

    private void test1() {
        HiLog.i(TAG, "test1", 2);
    }


    @Override
    public void setPresenter(UsbContract.Presenter presenter) {
        presenter.scan();
    }

    @Override
    public void onScanSuccess() {
        Log.i(TAG, "onScanSuccess() called thread: " + Thread.currentThread().getName());
    }
}