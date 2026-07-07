package com.victor.trello_clone.data.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

public class Pagination {

    @Schema(example = "0")
    private int page;

    @Schema(example = "10")
    private int size;

    @Schema(example = "35")
    private long totalElements;

    @Schema(example = "4")
    private int totalPages;

    public Pagination(int page, int size, long totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Pagination that)) return false;
        return getPage() == that.getPage() && getSize() == that.getSize() && getTotalElements() == that.getTotalElements() && getTotalPages() == that.getTotalPages();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPage(), getSize(), getTotalElements(), getTotalPages());
    }
}