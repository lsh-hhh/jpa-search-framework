package com.lsh.framework;


import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageDTO<T> {
    private int pageIndex;
    private int pageSize;
    private int totalPages;
    private long totalElements;
    private List<T> content = new ArrayList<>();

    public PageDTO(List<T> content, int pageIndex, int pageSize, long totalElements) {
        this.content = content;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
    }

    public PageDTO(List<T> content, BaseSearchDTO searchDTO, long total) {
        this.content = content;
        this.pageIndex = searchDTO.getPage();
        this.pageSize = searchDTO.getPageSize();
        this.totalElements = total;
    }

    public int getTotalPages() {
        return this.pageSize == 0 ? 0 : Double.valueOf(Math.ceil((double) this.totalElements / (double) this.pageSize)).intValue();
    }

    public PageDTO(Page page) {
        this.content = page.getContent();
        this.pageIndex = page.getNumber() + 1;
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
    }

    public PageDTO() {
    }
}
