package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.List;

/**
 * SizeISortStrategy
 * <p>
 * 按大小排序
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public class SizeSortStrategy implements ISortStrategy {

    SizeSortStrategy() {

    }

    @Override
    public List<FileSystemNode> getSortNodes(List<FileSystemNode> nodes) {
        // TODO: 2024/5/31 待实现
        return nodes;
    }
}
