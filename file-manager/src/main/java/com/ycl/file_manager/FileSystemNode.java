package com.ycl.file_manager;

import java.io.File;

/**
 * FileSystemNode
 * Created by Yclong on 2024/5/29.
 **/
public abstract class FileSystemNode {

    /**
     * 文件路径
     */
    protected String path;
    /**
     * 父节点
     */
    protected FileSystemNode parent;

    public FileSystemNode(String path) {
        this.path = path;
    }

    /**
     * 文件数量
     */
    public abstract int countNumOfFiles();

    /**
     * 文件大小
     */
    public abstract long countSizeOfFiles();


    public final String getPath() {
        return path;
    }

    /**
     * 文件名
     */
    public abstract String getFileName();

    /**
     * 重命名
     *
     * @param name 文件名
     */
    public abstract void rename(String name);

    /**
     * 删除
     */
    public abstract void delete();

    /**
     * 最后修改时间
     */
    public final long lastModified() {
        File file = new File(path);
        if (file.exists()) {
            return file.lastModified();
        }
        return -1;
    }
}
