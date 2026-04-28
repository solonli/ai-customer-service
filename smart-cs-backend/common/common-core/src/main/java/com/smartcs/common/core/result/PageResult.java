package com.smartcs.common.core.result;

import lombok.Data;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<T> records;
    private Long total;
    private Long size;
    private Long current;
    private Long pages;

    public PageResult() {
        this.records = Collections.emptyList();
    }

    public PageResult(List<T> records, Long total, Long size, Long current) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = size > 0 ? (total + size - 1) / size : 0L;
    }

    public static <T> PageResult<T> of(List<T> records, Long total, Long size, Long current) {
        return new PageResult<>(records, total, size, current);
    }
}
