package com.cc.blogserver.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页统一返回结果
 */
@Data
public class PageResult<T> implements Serializable {

    private List<T> records;
    private long total;
    private long current;
    private long size;

    public PageResult() {
    }

    public PageResult(List<T> records, long total, long current, long size) {
        this.records = records;
        this.total = total;
        this.current = current;
        this.size = size;
    }
}
