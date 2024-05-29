package com.ycl.ipc.client;

import java.io.File;
import java.util.List;

/**
 * FileSource
 * Created by Yclong on 2024/4/28.
 **/
public interface FileSource {

    /**
     * 扫描文件
     *
     * @param root
     * @return
     */
    List<File> scanFiles(File root);

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    boolean deleteFile(File file);

    /**
     * 复制文件
     *
     * @param src
     * @param dst
     * @return
     */
    boolean copyFile(File src, File dst);

    /**
     * 重命名文件
     *
     * @param file
     * @return
     */
    boolean renameFile(File file);

}
