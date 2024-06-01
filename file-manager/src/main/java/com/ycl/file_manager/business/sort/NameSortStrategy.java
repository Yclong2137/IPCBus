package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * NameSortStrategy
 * <p>
 * 按名称排序
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public class NameSortStrategy implements ISortStrategy {

    NameSortStrategy() {
    }


    @Override
    public List<FileSystemNode> getSortNodes(List<FileSystemNode> nodes) {
        Collections.sort(nodes, new Comparator<FileSystemNode>() {
            @Override
            public int compare(FileSystemNode o1, FileSystemNode o2) {
                return o1.getFileName().compareTo(o2.getFileName());
            }
        });
        return nodes;
    }
}
