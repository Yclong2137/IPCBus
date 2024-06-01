package com.ycl.file_manager;


import android.app.Application;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.creator.NodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.store.FileNodeStore;
import com.ycl.file_manager.business.store.IFileNodeStore;

import java.util.concurrent.Executors;

/**
 * FileViewModel
 * Created by Yclong on 2024/6/1
 **/
public class FileViewModel extends AndroidViewModel {

    private static final String TAG = "file_mgr_FileViewModel";

    private final MutableLiveData<FileSystemNode> mObservableRootNode = new MutableLiveData<>();

    private final String rootPath;

    private final IFileNodeStore mFileNodeStore = new FileNodeStore();

    public FileViewModel(@NonNull Application application) {
        super(application);
        Log.d(TAG, "FileViewModel() called with: application = [" + application + "]");
        rootPath = Environment.getExternalStorageDirectory().getPath();
        Log.d(TAG, "FileViewModel() called with: rootPath = [" + rootPath + "]");

    }

    /**
     * 加载文件节点
     *
     * @param filter 过滤器
     */
    public void loadFileNodes(@NonNull INodeFilter filter) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                FileSystemNode node = mFileNodeStore.scan(rootPath, NodeCreator.DEFAULT, filter);
                mObservableRootNode.postValue(node);
            }
        });
    }


    public LiveData<FileSystemNode> getObservableRootNode() {
        return mObservableRootNode;
    }
}
