package com.ycl.file_manager.scan;

import android.os.RemoteException;

import com.ycl.file_manager.DirectoryNode;
import com.ycl.file_manager.FileNode;
import com.ycl.file_manager.FileSystemNode;

import java.io.File;
import java.io.FileFilter;

/**
 * FileScanManager
 * Created by Yclong on 2024/5/29.
 **/
public class FileScanManager extends IFileScanManager.Stub {

    /**
     * 扫描文件
     *
     * @param rootPath
     * @param listener
     * @throws RemoteException
     */
    @Override
    public void scan(String rootPath, IFileScanListener listener) throws RemoteException {
        File dirFile = new File(rootPath);
        if (!dirFile.exists()) {
            return;
        }
        FileSystemNode root;
        if (dirFile.isDirectory()) {
            root = new DirectoryNode(dirFile.getPath());
        } else {
            root = new FileNode(dirFile.getPath());
        }
        FileSystemNode node = scanFile(root);
        System.out.println("文件数量：" + node.countNumOfFiles());
        System.out.println("文件大小：" + node.countSizeOfFiles());

    }

    /**
     * 扫描文件
     *
     * @param root 根节点
     * @return
     */
    private FileSystemNode scanFile(FileSystemNode root) {
        File dirFile = new File(root.getPath());
        if (!dirFile.isDirectory()) {
            return root;
        }
        File[] files = dirFile.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                // TODO: 2024/5/29 过滤指定文件
                System.out.println(".accept   pathname = " + pathname);
                return true;
            }
        });
        if (files == null) {
            return root;
        }
        for (File file : files) {

            FileSystemNode child;

            if (file.isDirectory()) {
                child = scanFile(new DirectoryNode(file.getPath()));
            } else {
                child = new FileNode(file.getPath());
            }
            if (root instanceof DirectoryNode) {
                ((DirectoryNode) root).addSubNode(child);
            }
        }
        return root;
    }


}
