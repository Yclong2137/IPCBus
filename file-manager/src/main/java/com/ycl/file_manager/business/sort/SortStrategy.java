package com.ycl.file_manager.business.sort;

/**
 * SortStrategy
 * Created by Yclong on 2024/5/31.
 **/
public interface SortStrategy {
    /**
     * 默认
     */
    ISortStrategy NONE = new NoneSortStrategy();

    /**
     * 按名称排序
     */
    ISortStrategy NAME = new NameSortStrategy();
    /**
     * 按时间排序
     */
    ISortStrategy TIME = new TimeSortStrategy();
    /**
     * 按大小排序
     */
    ISortStrategy SIZE = new SizeSortStrategy();


}
