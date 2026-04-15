package com.damc.legalnotices.dto.user;

import com.damc.legalnotices.dto.DatatableDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditTransactionListDto extends DatatableDto {

    private Long userid;

    @Override
    public String getSortColumn() {
        return super.getSortColumn() == null ? "createdAt" : super.getSortColumn();
    }
}
