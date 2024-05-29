package com.ycl.file_manager;

import java.io.File;

/**
 * File
 * Created by Yclong on 2024/5/29.
 **/
public class FileNode extends FileSystemNode {

    public FileNode(String path) {
        super(path);
    }

    @Override
    public int countNumOfFiles() {
        return 1;
    }

    @Override
    public long countSizeOfFiles() {
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    @Override
    public String getFileName() {
        File file = new File(path);
        if (file.exists()) {
            //todo 处理后缀名
            return file.getName();
        }
        return null;
    }

    @Override
    public void rename(String name) {
        // TODO: 2024/5/29
        //1.修改文件名
        //2.更新文件修改时间

    }

    @Override
    public void delete() {
        // TODO: 2024/5/29 直接删除文件即可
    }
}
