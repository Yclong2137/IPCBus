package com.ycl.file_manager.business.filter;


import com.ycl.file_manager.business.tree.AudioFileNode;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileSystemNode;

/**
 * PictureFilter
 * <p>
 * 音频节点过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public class AudioNodeFilter extends KeywordNodeFilter {

    AudioNodeFilter() {
    }

    @Override
    public boolean doFilter(FileSystemNode node) {
        boolean isAudio = node instanceof AudioFileNode;
        return isAudio && super.doFilter(node);
    }

    @Override
    public String getName() {
        return "Audio";
    }
}
