package com.notices.domain.dao;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DataTableDao<T> {
    private int draw;
    private long recordsTotal;
    private long recordsFiltered;
    private T data;
}
