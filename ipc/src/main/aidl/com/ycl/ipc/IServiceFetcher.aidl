// IServiceFetcher.aidl
package com.ycl.ipc;

// Declare any non-default types here with import statements

interface IServiceFetcher {
    IBinder getService(String name);
}