package com.ycl.file_manager.business.filter;


/**
 * Filter
 * <p>
 * 用于节点过滤
 * <p>
 * Created by Yclong on 2024/5/31.
 **/
public interface NodeFilter {

    /**
     * 默认
     */
    INodeFilter NONE = new NoneNodeFilter();

    /**
     * 关键字过滤器
     */
    KeywordNodeFilter KEYWORD = new KeywordNodeFilter();
    /**
     * 音频关键字过滤器
     */
    KeywordNodeFilter AUDIO = new AudioNodeFilter();
    /**
     * 视频关键字过滤器
     */
    KeywordNodeFilter VIDEO = new VideoNodeFilter();
    /**
     * 图片关键字过滤器
     */
    KeywordNodeFilter PICTURE = new PictureNodeFilter();


}
