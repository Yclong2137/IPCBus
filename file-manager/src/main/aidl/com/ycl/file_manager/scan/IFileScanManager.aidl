// IFileScanManager.aidl
package com.ycl.file_manager.scan;

import com.ycl.file_manager.scan.IFileScanListener;

// Declare any non-default types here with import statements

interface IFileScanManager {

    void scan(String rootPath,IFileScanListener listener);

}