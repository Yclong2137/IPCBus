package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.List;

/**
 * NoneSortStrategy
 * <p>
 * 无需排序
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public class NoneSortStrategy implements ISortStrategy {

    NoneSortStrategy() {

    }

    @Override
    public List<FileSystemNode> getSortNodes(List<FileSystemNode> nodes) {
        return nodes;
    }

    @Override
    public String getName() {
        return "None";
    }
}
