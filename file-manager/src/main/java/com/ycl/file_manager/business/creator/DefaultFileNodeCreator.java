package com.ycl.file_manager.business.creator;

import com.ycl.file_manager.business.tree.AudioFileNode;
import com.ycl.file_manager.business.tree.DirectoryNode;
import com.ycl.file_manager.business.tree.FileNode;
import com.ycl.file_manager.business.tree.FileSystemNode;
import com.ycl.file_manager.business.tree.PictureFileNode;
import com.ycl.file_manager.business.tree.VideoFileNode;

import java.io.File;
import java.util.regex.Pattern;

/**
 * DefaultFileNodeCreator
 * <p>
 * 默认支持节点构建器
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public final class DefaultFileNodeCreator implements IFileNodeCreator {

    /**
     * 图片格式
     */
    private static final Pattern PICTURE_PATTERN = Pattern.compile(".+\\.((jpg)|(png)|(bmp)|(jpeg)|(gif))$");
    /**
     * 音频格式
     */
    private static final Pattern AUDIO_PATTERN = Pattern.compile(".+\\.((mp3)|(wma)|(aac)|(wav)|(flac)|(ogg)|(m4a))$");
    /**
     * 视频格式
     */
    private static final Pattern VIDEO_PATTERN = Pattern.compile(".+\\.((wmv)|(mp4)|(3gp)|(asf)|(rmvb)|(rm)|(flv)|(m4v)|(mov)|(vob)|(mpg)|(mkv))$");

    DefaultFileNodeCreator() {
    }


    @Override
    public FileSystemNode create(File file) {
        if (file.isDirectory()) {
            return new DirectoryNode(file.getPath());
        }
        String fileName = file.getName().toLowerCase();
        if (PICTURE_PATTERN.matcher(fileName).matches()) {
            return new PictureFileNode(file.getPath());
        }
        if (AUDIO_PATTERN.matcher(fileName).matches()) {
            return new AudioFileNode(file.getPath());
        }
        if (VIDEO_PATTERN.matcher(fileName).matches()) {
            return new VideoFileNode(file.getPath());
        }
        return new FileNode(file.getPath());
    }
}
