package com.victor.trello_clone.data.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {

    private List<T> data;
    private Pagination pagination;

    public PageResponse(Page<T> page) {
        this.data = page.getContent();
        this.pagination = new Pagination(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages()
        );
    }

    public List<T> getData() {
        return data;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }
}