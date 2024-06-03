package com.ycl.file_manager.business.sort;

import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

import java.util.Collections;
import java.util.Comparator;
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
        Collections.sort(nodes, new Comparator<FileSystemNode>() {
            @Override
            public int compare(FileSystemNode o1, FileSystemNode o2) {
                //文件夹排在前面
                if (o1 instanceof DirectoryNode && !(o2 instanceof DirectoryNode)) {
                    return -1;
                }
                if (!(o1 instanceof DirectoryNode) && (o2 instanceof DirectoryNode)) {
                    return 1;
                }
                return (int) (o1.sizeOfFiles() - o2.sizeOfFiles());
            }
        });
        return nodes;
    }
}
