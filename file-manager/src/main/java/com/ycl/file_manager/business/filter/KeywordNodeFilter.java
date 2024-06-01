package com.ycl.file_manager.business.filter;


import com.ycl.file_manager.business.tree.FileSystemNode;

/**
 * KeywordFilter
 * <p>
 * 关键字过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public class KeywordNodeFilter implements INodeFilter {
    /**
     * 关键字
     */
    String keyword;

    KeywordNodeFilter() {
    }


    public KeywordNodeFilter keyword(String keyword) {
        this.keyword = keyword;
        return this;
    }

    @Override
    public boolean doFilter(FileSystemNode node) {
        // TODO: 2024/5/31 待实现
        if (keyword == null || keyword.isEmpty()) {
            return true;
        }
        String fileName = node.getFileName();
        if (fileName == null) {
            return true;
        }
        return fileName.contains(keyword);
    }
}
