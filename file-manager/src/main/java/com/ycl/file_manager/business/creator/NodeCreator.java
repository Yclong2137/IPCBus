package com.ycl.file_manager.business.creator;

/**
 * NodeCreator
 * <p>
 * 默认节点构建器
 * <p>
 * Created by Yclong on 2024/6/1
 **/
public interface NodeCreator {

    IFileNodeCreator DEFAULT = new DefaultFileNodeCreator();

}
