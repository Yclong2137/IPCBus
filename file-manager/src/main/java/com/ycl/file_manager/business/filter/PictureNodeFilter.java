package com.ycl.file_manager.business.filter;


import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.tree.PictureFileNode;

/**
 * PictureFilter
 * <p>
 * 图片节点过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public class PictureNodeFilter extends KeywordNodeFilter {


    PictureNodeFilter() {
    }

    @Override
    public boolean doFilter(FileSystemNode node) {
        boolean isPic = node instanceof PictureFileNode;
        return isPic && super.doFilter(node);
    }

    @Override
    public String getName() {
        return "Picture";
    }
}
