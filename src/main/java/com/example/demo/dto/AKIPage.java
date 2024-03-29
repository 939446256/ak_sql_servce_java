package com.example.demo.dto;

import lombok.Data;

import java.util.List;

@Data
public class AKIPage<T> {

    private List<T> records;
    // 一页数量
    private long size;

    // 数据总数
    private long total;

    // 当前页
    private long current;

    // 页数
    private long pages;

    public long offset() {
        long current = this.getCurrent();
        return current <= 1L ? 0L : Math.max((current - 1L) * this.getSize(), 0L);
    }

    long getPages() {
        if (this.getSize() == 0L) {
            return 0L;
        } else {
            long pages = this.getTotal() / this.getSize();
            if (this.getTotal() % this.getSize() != 0L) {
                ++pages;
            }
            return pages;
        }
    }
}
