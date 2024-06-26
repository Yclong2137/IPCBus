package com.ycl.file_manager.business.tree;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.FileNodeOp;
import com.ycl.file_manager.business.creator.IFileNodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * FileSystemNode
 * Created by Yclong on 2024/5/29.
 **/
public abstract class FileSystemNode implements FileNodeOp {

    /**
     * The file copy buffer size (30 MB)
     */
    private static final int FILE_COPY_BUFFER_SIZE = 1024 * 1024 * 30;


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
    public abstract int numOfFiles();

    /**
     * 文件数量
     *
     * @param filter 过滤器
     */
    public abstract int numOfFiles(@NonNull INodeFilter filter);

    /**
     * 文件大小
     */
    public abstract long sizeOfFiles();

    /**
     * 文件大小
     *
     * @param filter 过滤器
     */
    public abstract long sizeOfFiles(@NonNull INodeFilter filter);

    /**
     * 文件路径
     */
    public final String getPath() {
        return path;
    }

    /**
     * 文件名
     */
    public abstract String getFileName();

    /**
     * 设置修改时间
     *
     * @param time     时间戳
     * @param onlySelf true 只更新自己 false 更新父节点和自己
     */
    protected final void setLastModified(long time, boolean onlySelf) {
        File file = new File(path);
        if (file.setLastModified(time) && !onlySelf) {
            //同步修改上级目录的改动时间
            if (parent != null) {
                parent.setLastModified(time, false);
            }
        }
    }

    /**
     * 最后修改时间
     */
    public final long lastModified() {
        File file = new File(path);
        return file.lastModified();
    }

    /**
     * 父目录
     */
    public FileSystemNode getParent() {
        return parent;
    }

    /**
     * Return the extension of file.
     *
     * @param filePath The path of file.
     * @return the extension of file
     */
    protected final String getFileExtension(final String filePath) {
        if (isSpace(filePath)) {
            return "";
        }
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    private boolean isSpace(String path) {
        return path == null || path.isEmpty();
    }

    /**
     * 拷贝文件
     *
     * @param source      源文件
     * @param destination 目标文件
     * @throws IOException
     */
    protected void copyFile(@NonNull File source, @NonNull File destination) throws IOException {

        try (FileInputStream fis = new FileInputStream(source);
             FileOutputStream fos = new FileOutputStream(destination);
             FileChannel input = fis.getChannel();
             FileChannel output = fos.getChannel()) {

            long size = input.size();
            long pos = 0;
            long count;
            while (pos < size) {
                count = size - pos > FILE_COPY_BUFFER_SIZE ? FILE_COPY_BUFFER_SIZE : size - pos;
                pos += output.transferFrom(input, pos, count);
            }
        }
    }

    /**
     * 节点创建工厂
     */
    public static abstract class Factory {

        public abstract FileSystemNode create(File file, @NonNull IFileNodeCreator fileNodeCreator);
    }

    public static final Factory FACTORY = new Factory() {

        @Override
        public FileSystemNode create(File file, @NonNull IFileNodeCreator fileNodeCreator) {
            return fileNodeCreator.create(file);
        }

    };


}
