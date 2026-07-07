package com.victor.trello_clone.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {

    @Schema(description = "List of returned items")
    private List<T> data;

    @Schema(description = "Pagination metadata")
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