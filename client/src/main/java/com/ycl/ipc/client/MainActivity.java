package com.ycl.ipc.client;


import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.ycl.ipc.bus.IPCSingleton;
import com.ycl.sdk_base.IActivityManager;
import com.ycl.sdk_base.ICarListener;
import com.ycl.sdk_base.bean.VideoViewAngleData;

public class MainActivity extends Activity {

    IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);
    private static final String TAG = ">>>>>>>>>>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IActivityManager iActivityManager = singleton.get();
                if (iActivityManager != null) {
                    VideoViewAngleData data = new VideoViewAngleData();
                    data.id = 1;
                    data.retCode = 2;
                    data.videoSupervisionAVMView = 3;
                    data.videoSupervisionAVMViewFRes = 4;
                    data.videoSupervisionAVMViewRes = 5;
                String packageName = iActivityManager.getPackageName(99, data);
                System.out.println(">>>>>>>>>>>>> " + packageName);
//                iActivityManager.register(new IServiceFetcher.Stub() {
//                    @Override
//                    public IBinder getService(String name) throws RemoteException {
//                        Log.i(TAG, "getService() called with: name = [" + name + "]");
//                        return null;
//                    }
//                });
                    iActivityManager.register11(new ICarListener() {

                        @Override
                        public void test(int a) {
                            Log.i(TAG, "test() called with: a = [" + a + "]");
                        }
                    });
                }
            }
        });
    }
}