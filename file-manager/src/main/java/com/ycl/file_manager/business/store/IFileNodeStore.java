package com.ycl.file_manager.business.store;

import androidx.annotation.NonNull;

import com.ycl.file_manager.business.creator.IFileNodeCreator;
import com.ycl.file_manager.business.filter.INodeFilter;
import com.ycl.file_manager.business.tree.FileSystemNode;

/**
 * IFileNodeStore
 * Created by Yclong on 2024/6/1
 **/
public interface IFileNodeStore {

    /**
     * 扫描
     *
     * @param rootPath    根目录
     * @param nodeCreator 节点创建器
     * @param nodeFilter  节点过滤器
     * @return 根节点
     */
    FileSystemNode scan(String rootPath, @NonNull IFileNodeCreator nodeCreator, @NonNull INodeFilter nodeFilter);

    /**
     * 设置扫描状态监听器
     *
     * @param onScanStateListener 监听器
     */
    void setOnScanStateListener(OnScanStateListener onScanStateListener);

    /**
     * 扫描状态监听器
     */
    interface OnScanStateListener {
        /**
         * 开始扫描
         */
        default void onScanStart() {

        }

        /**
         * 扫描进度改变
         *
         * @param node     当前扫描到的节点
         * @param progress 扫描进度
         */
        default void onScanProgressChanged(FileSystemNode node, int progress) {

        }

        /**
         * 扫描成功
         *
         * @param root 根节点
         */
        void onScanSuccess(FileSystemNode root);

        /**
         * 扫描失败
         *
         * @param code 错误码
         * @param msg  错误文言
         */
        void onScanFailure(int code, String msg);

        /**
         * 扫描完成（一定会执行）
         */
        default void onFinished() {

        }

    }

}
