package com.ycl.file_manager.business.store;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.creator.IFileNodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;

import java.io.File;

/**
 * FileNodeStore
 * Created by Yclong on 2024/6/1
 **/
public class FileNodeStore implements IFileNodeStore {

    @Override
    public FileSystemNode scan(String rootPath, @NonNull IFileNodeCreator nodeCreator, @NonNull INodeFilter nodeFilter) {
        File dirFile = new File(rootPath);
        if (!dirFile.exists()) {
            return null;
        }
        //构建根节点
        FileSystemNode root = FileSystemNode.DEFAULT.create(dirFile, nodeCreator);
        //扫描文件
        return scanFile(root, nodeCreator, nodeFilter);
    }

    /**
     * 扫描文件，构建节点树
     *
     * @param root 根节点
     */
    private FileSystemNode scanFile(FileSystemNode root, @NonNull IFileNodeCreator nodeCreator, @NonNull INodeFilter nodeFilter) {
        if (root == null) {
            return null;
        }
        File dirFile = new File(root.getPath());
        if (!dirFile.isDirectory()) {
            return root;
        }
        File[] files = dirFile.listFiles();
        if (files == null || files.length == 0) {
            return root;
        }
        for (File file : files) {

            FileSystemNode child;

            FileSystemNode node = FileSystemNode.DEFAULT.create(file, nodeCreator);

            if (file.isDirectory()) {
                child = scanFile(node, nodeCreator, nodeFilter);
            } else {
                child = node;
            }
            if (root instanceof DirectoryNode) {
                //文件夹中没有文件则不处理
                if (child.numOfFiles(nodeFilter) > 0) {
                    ((DirectoryNode) root).addSubNode(child);
                }
            }
        }
        return root;
    }

}
