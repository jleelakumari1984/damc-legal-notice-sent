package com.damc.legalnotices.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatatableDto {
    private boolean allData;
    private int dtStart;
    private int dtLength;
    private int dtDraw;
    private String sortColumn;
    private String sortDirection;

    @JsonIgnore
    public Pageable getPagination() {

        int length = dtLength <= 0 ? 10 : dtLength;
        int start = dtStart <= 0 ? 0 : dtStart;
        String sortColumn = getSortColumn();
        String sortDirection = this.sortDirection != null ? this.sortDirection : "DESC";
        if (length <= 10)
            length = 10;
        if (length > 1000)
            length = 1000;
        int page = start / length;
        Pageable pageable = PageRequest.of(page, length, Sort.by(Sort.Direction.fromString(sortDirection), sortColumn));
        return pageable;
    }

    public int getDraw() {
        return dtDraw <= 0 ? 1 : dtDraw;
    }

    public int getStart() {
        return dtStart <= 0 ? 0 : dtStart;
    }

    public int getLength() {
        int length = dtLength <= 0 ? 10 : dtLength;
        if (length <= 10)
            length = 10;
        if (length > 1000)
            length = 1000;
        return length;
    }
}
