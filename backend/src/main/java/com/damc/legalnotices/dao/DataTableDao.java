package com.damc.legalnotices.dao;

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
