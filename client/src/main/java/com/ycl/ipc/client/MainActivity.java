package com.ycl.ipc.client;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.ycl.file_manager.scan.FileScanManager;
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
        ImageView iv = findViewById(R.id.iv);
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    new FileScanManager().scan(getCacheDir().getPath(),null);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
        Glide.with(this)
                .load("https://pics4.baidu.com/feed/1f178a82b9014a90f3c86d16743d3d1cb21bee7b.jpeg@f_auto?token=e05b7f45ae5afad6da001714d8a3b9dc")
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.i(TAG, "onLoadFailed() called with: e = [" + e + "], model = [" + model + "], target = [" + target + "], isFirstResource = [" + isFirstResource + "]");
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.i(TAG, "onResourceReady() called with: resource = [" + resource + "], model = [" + model + "], target = [" + target + "], dataSource = [" + dataSource + "], isFirstResource = [" + isFirstResource + "]");
                        return false;
                    }
                })
                .into(iv);
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, 1234);
            }
        }
        startService(new Intent(this, MyService.class));
        iActivityManager = singleton.get();
        new UsbPresenter(this);
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

        test1();

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