package com.ycl.file_manager.business.filter;/*
 * null.java
 * Created by Yclong on 2024/6/1
 **/

import com.ycl.file_manager.business.tree.FileSystemNode;

/**
 * PictureFilter
 * <p>
 * 无需过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public class NoneNodeFilter implements INodeFilter {

    NoneNodeFilter() {
    }

    @Override
    public boolean doFilter(FileSystemNode node) {
        return true;
    }

    @Override
    public String getName() {
        return "None";
    }
}
