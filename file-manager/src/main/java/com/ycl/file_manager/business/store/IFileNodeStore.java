package com.ycl.file_manager.business.store;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.creator.IFileNodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;

/**
 * IFileNodeStore
 * Created by Yclong on 2024/6/1
 **/
public interface IFileNodeStore {

    /**
     * 扫描
     *
     * @param rootPath    根目录
     * @param nodeCreator 节点创建器
     * @param nodeFilter  节点过滤器
     * @return 根节点
     */
    FileSystemNode scan(String rootPath, @NonNull IFileNodeCreator nodeCreator, @NonNull INodeFilter nodeFilter);

}
