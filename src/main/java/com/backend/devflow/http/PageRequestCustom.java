package com.backend.devflow.http;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequestCustom {
    private String sort;
    private String filter;
    private int page;
    private int pageSize;

    public PageRequestCustom() {
        this.sort = "createdAt,desc"; // Default sort by createdAt in descending order
        this.filter = "";
        this.page = 0; // Default to the first page
        this.pageSize = 10; // Default page size
    }
}
