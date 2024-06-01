package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.List;

/**
 * TimeISortStrategy
 * <p>
 * 按时间排序
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public class TimeSortStrategy implements ISortStrategy {

    TimeSortStrategy() {

    }

    @Override
    public List<FileSystemNode> getSortNodes(List<FileSystemNode> nodes) {
        // TODO: 2024/5/31 待实现
        return nodes;
    }

}
