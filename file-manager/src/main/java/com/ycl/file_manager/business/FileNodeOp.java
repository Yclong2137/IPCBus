package com.ycl.file_manager.business;

/**
 * FileNodeAction
 * Created by Yclong on 2024/5/31.
 **/
public interface FileNodeOp {


    /**
     * 重命名
     *
     * @param name 文件名
     */
    boolean rename(String name);


    /**
     * 删除
     */
    boolean delete();

    /**
     * 拷贝
     *
     * @param dstPath 目标路径
     */
    boolean copy(String dstPath);

}
