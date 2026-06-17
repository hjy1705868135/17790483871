package com.md.basePlatform.domain;

import java.util.Collections;
import java.util.List;

/**
 * 简单分页结果（视图层与 Service 共用）。
 *
 * @param <T> 记录类型
 */
public class PageResult<T> {

    private List<T> records = Collections.emptyList();
    private long total;
    private int pageNum = 1;
    private int pageSize = 10;

    /**
     * @return 当前页数据
     */
    public List<T> getRecords() {
        return records;
    }

    /**
     * @param records 当前页数据
     */
    public void setRecords(List<T> records) {
        this.records = records;
    }

    /**
     * @return 总条数
     */
    public long getTotal() {
        return total;
    }

    /**
     * @param total 总条数
     */
    public void setTotal(long total) {
        this.total = total;
    }

    /**
     * @return 当前页码（从 1 开始）
     */
    public int getPageNum() {
        return pageNum;
    }

    /**
     * @param pageNum 当前页码
     */
    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    /**
     * @return 每页条数
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * @param pageSize 每页条数
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * @return 总页数
     */
    public int getTotalPages() {
        if (pageSize <= 0) {
            return 0;
        }
        return (int) ((total + pageSize - 1) / pageSize);
    }
}
