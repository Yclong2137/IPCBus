package com.ycl.file_manager.business.store;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.ErrorCodes;
import com.ycl.file_manager.business.creator.IFileNodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * FileNodeStore
 * Created by Yclong on 2024/6/1
 **/
public class FileNodeStore implements IFileNodeStore {

    /**
     * 已扫描数量
     */
    private final AtomicInteger mScanNum = new AtomicInteger();

    /**
     * 监听器
     */
    private OnScanStateListener mOnScanStateListener;

    @Override
    public FileSystemNode scan(String rootPath, @NonNull IFileNodeCreator nodeCreator, @NonNull INodeFilter nodeFilter) {
        FileSystemNode node = null;
        mScanNum.set(0);
        try {
            if (mOnScanStateListener != null) {
                mOnScanStateListener.onScanStart();
            }
            File dirFile = new File(rootPath);
            if (!dirFile.exists()) {
                if (mOnScanStateListener != null) {
                    mOnScanStateListener.onScanFailure(ErrorCodes.FILE_NO_FOUND, "文件不存在");
                }
                return null;
            }
            //构建根节点
            FileSystemNode root = FileSystemNode.FACTORY.create(dirFile, nodeCreator);
            //扫描文件
            node = scanFile(root, nodeCreator, nodeFilter);
            if (mOnScanStateListener != null) {
                mOnScanStateListener.onScanSuccess(node);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (mOnScanStateListener != null) {
                mOnScanStateListener.onScanFailure(ErrorCodes.SCAN_ERROR, "扫描失败");
            }
        } finally {
            if (mOnScanStateListener != null) {
                mOnScanStateListener.onFinished();
            }
        }
        return node;
    }

    /**
     * 设置扫描状态监听器
     *
     * @param onScanStateListener 监听器
     */
    @Override
    public void setOnScanStateListener(OnScanStateListener onScanStateListener) {
        this.mOnScanStateListener = onScanStateListener;
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
        //如果不是文件夹则无需处理
        if (!dirFile.isDirectory()) {
            return root;
        }
        File[] files = dirFile.listFiles();
        //文件夹中无文件则无需处理
        if (files == null || files.length == 0) {
            return root;
        }
        //遍历文件，开始扫描
        for (File file : files) {

            FileSystemNode child = null;

            FileSystemNode node = FileSystemNode.FACTORY.create(file, nodeCreator);

            if (file.isDirectory()) {
                //如果是文件夹则递归扫描
                child = scanFile(node, nodeCreator, nodeFilter);
            } else {
                //处理文件
                if (nodeFilter.doFilter(node)) {
                    child = node;
                    if (mOnScanStateListener != null) {
                        mOnScanStateListener.onScanProgressChanged(node, mScanNum.incrementAndGet());
                    }
                }

            }
            //构建节点树
            if (root instanceof DirectoryNode && child != null) {
                //文件夹中没有文件则不处理
                if (child.numOfFiles(nodeFilter) > 0) {
                    ((DirectoryNode) root).addSubNode(child);
                }
            }
        }

        return root;
    }

}
