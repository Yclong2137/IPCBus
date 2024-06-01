package com.ycl.file_manager.business.filter;


import com.ycl.file_manager.business.tree.FileSystemNode;

/**
 * Filter
 * Created by Yclong on 2024/5/31.
 **/
public interface INodeFilter {

    /**
     * 过滤
     *
     * @param node 节点
     */
    boolean doFilter(FileSystemNode node);


}
