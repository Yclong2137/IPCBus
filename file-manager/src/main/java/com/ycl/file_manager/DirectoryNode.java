package com.ycl.file_manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Directory
 * Created by Yclong on 2024/5/29.
 **/
public class DirectoryNode extends FileSystemNode {

    private final List<FileSystemNode> subNodes = new ArrayList<>();

    public DirectoryNode(String path) {
        super(path);
    }

    @Override
    public int countNumOfFiles() {
        // TODO: 2024/5/29 可做优化，无需每次都计算
        int numOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            numOfFiles += subNode.countNumOfFiles();
        }
        return numOfFiles;
    }

    @Override
    public long countSizeOfFiles() {
        // TODO: 2024/5/29 可做优化，无需每次都计算
        long sizeOfFiles = 0;
        for (FileSystemNode subNode : subNodes) {
            sizeOfFiles += subNode.countSizeOfFiles();
        }
        return sizeOfFiles;
    }

    @Override
    public String getFileName() {
        File file = new File(path);
        if (file.exists()) {
            return file.getName();
        }
        return null;
    }

    @Override
    public void rename(String name) {
        // TODO: 2024/5/29 重命名
        //1.重命名（没有后缀）
        //2.更改时间
    }

    @Override
    public void delete() {
        // TODO: 2024/5/29 遍历删除文件
    }

    /**
     * 新增子节点
     *
     * @param fileOrDir 子节点
     */
    public void addSubNode(FileSystemNode fileOrDir) {
        fileOrDir.parent = this;
        this.subNodes.add(fileOrDir);
    }

    /**
     * 移除子节点
     *
     * @param fileOrDir 子节点
     */
    public void removeSubNode(FileSystemNode fileOrDir) {
        int size = subNodes.size();
        int i = 0;
        for (; i < size; ++i) {
            if (subNodes.get(i).getPath().equalsIgnoreCase(fileOrDir.getPath())) {
                break;
            }
        }
        if (i < size) {
            subNodes.remove(i);
        }
    }

}
