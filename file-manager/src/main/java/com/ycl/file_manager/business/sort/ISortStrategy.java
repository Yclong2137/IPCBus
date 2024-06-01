package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.List;


/**
 * SortStrategy
 * <p>
 * 用于节点排序
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public interface ISortStrategy {

    /**
     * 排序
     *
     * @param nodes 待排序数据集
     * @return 已排序的数据集
     */
    List<FileSystemNode> getSortNodes(List<FileSystemNode> nodes);


}
