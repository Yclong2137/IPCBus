package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.Collections;
import java.util.Comparator;
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
        Collections.sort(nodes, new Comparator<FileSystemNode>() {
            @Override
            public int compare(FileSystemNode o1, FileSystemNode o2) {
                return (int) (o1.lastModified() - o2.lastModified());
            }
        });
        return nodes;
    }

}
