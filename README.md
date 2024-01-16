#### IPC方案
1、使用
```java
//Client
//1、初始化
IPCBus.initialize(new IServerCache() {

            @Override
            public void addBinderStub(String serverName, IBinder binder) {
                ServiceCache.addService(serverName, binder);
            }

            @Override
            public IBinder getBinderProxy(String serverName) {
                return ServiceManagerNative.getService(serverName);
            }

            @Override
            public IBinder getBinderStub(String serverName) {
                return ServiceCache.getService(serverName);
            }

        });
//2、使用
IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);


void test(){
    IActivityManager iActivityManager = singleton.get();
    if(iActivityManager!=null){
        VideoViewAngleData data = new VideoViewAngleData();
        data.id = 1;
        data.retCode = 2;
        data.videoSupervisionAVMView = 3;
        data.videoSupervisionAVMViewFRes = 4;
        data.videoSupervisionAVMViewRes = 5;
        String packageName = iActivityManager.getPackageName(99, data);
        System.out.println(">>>>>>>>>>>>> " + packageName);

        iActivityManager.register11(new ICarListener() {

             @Override
             public void test(int a) {
                    Log.i(TAG, "test() called with: a = [" + a + "]");
            }
            });
    }
}


//Server
//1、初始化
IPCBus.register(IActivityManager.class, new IActivityManager() {
            @Override
            public String getPackageName(int a, VideoViewAngleData data) {
                Log.i(TAG, "getPackageName() called with: a = [" + a + "], data = [" + data + "]");
                return "这是测试包名";
            }

            @Override
            public void register11(ICarListener iCarListener) {
                Log.i(TAG, "register11() called with: iCarListener = [" + iCarListener + "]");
                callbackList.register(iCarListener);
                //throw new IllegalStateException("+++++++++++++++++");
                callbackList.call(new RemoteCallbackListExt.ItemCallback<ICarListener>() {
                    @Override
                    public void invokeItem(ICarListener item) {
                        iCarListener.test(3333333);

                    }

                });
            }
        });

//2、提供服务
private static class ServiceFetcher extends IServiceFetcher.Stub {
        @Override
        public IBinder getService(String name) throws RemoteException {
            Log.i(TAG, "getService() called with: name = [" + name + "]");
            if (name != null) {
                return IPCBus.getBinderStub(name);
            }
            return null;
        }

    }
```
