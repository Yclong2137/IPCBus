package com.ycl.file_manager.business.creator;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.io.File;

/**
 * Filter
 * <p>
 * 根据不同车型处理相应节点的创建工作
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public interface IFileNodeCreator {

    /**
     * 创建相应节点
     *
     * @param file 文件
     * @return 节点
     */
    FileSystemNode create(File file);


}
