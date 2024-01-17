## IPCBus（无aidl实现）

### 1、普通IPC通信

* 需要编写aidl文件
* 无法重载
* Binder线程池过载，导致阻塞


### 2、特性

* 无需编写任何aidl文件
* 对oneway功能的支持
* 支持接口方法不一致导致的错误输出提示（仅针对非oneway）
* 反注册功能的支持
* 服务断连后自动重新获取服务
* 支持方法重载
* 优化Binder线程池过载导致ipc无法执行（仅针对oneway）
* 对接口统一管理（输入方法参数以及返回值等。。。）

### 3、todo
* 对out、inout实现

### 4、使用
##### Client
```java

//1、初始化
IPCBus.initialize(new IServerCache() {

            @Override
            public IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
                if (IServiceFetcher.class == interfaceClass) {
                    return ServiceManagerNative.getServiceFetcherBinder();
                }
                return ServiceManagerNative.getService(serverName);
            }


});
//2、使用
IPCSingleton<IActivityManager> singleton = new IPCSingleton<>(IActivityManager.class);


VideoViewAngleData data = new VideoViewAngleData();
                    data.id = 1;
                    data.retCode = 2;
                    data.videoSupervisionAVMView = 3;
                    data.videoSupervisionAVMViewFRes = 4;
                    data.videoSupervisionAVMViewRes = 5;

//同步调用，parcelable参数
iActivityManager.getPackageName(99, data);
//同步调用，parcelable参数,重载
iActivityManager.getPackageName(data)


ICarListener iCarListener = ...;
//同步调用
iActivityManager.register(iCarListener);

//异步调用
@Oneway
iActivityManager.register(iCarListener);

//同步调用
@Unsubscribe
iActivityManager.unregister(iCarListener);

//异步调用
@Oneway
@Unsubscribe
iActivityManager.unregister(iCarListener);

```

Server
```java

//初始化
IPCBus.initialize(new IServerCache() {

            @Override
            public IBinder queryBinderProxy(Class<?> interfaceClass, String serverName) {
                return null;
            }
            
        });

//注册服务
IPCBus.register(IActivityManager.class, new IActivityManager() {
            ...
        });


```

