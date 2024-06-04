package com.ycl.file_manager.business.filter;


import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.tree.VideoFileNode;

/**
 * PictureFilter
 * <p>
 * 视频节点过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public class VideoNodeFilter extends KeywordNodeFilter {

    VideoNodeFilter() {
    }

    @Override
    public boolean doFilter(FileSystemNode node) {
        boolean isVideo = node instanceof VideoFileNode;
        return isVideo && super.doFilter(node);
    }

    @Override
    public String getName() {
        return "Video";
    }
}
