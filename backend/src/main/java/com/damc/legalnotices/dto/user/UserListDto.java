package com.damc.legalnotices.dto.user;

import com.damc.legalnotices.dto.DatatableDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListDto extends DatatableDto {

    @Override
    public String getSortColumn() {
        return super.getSortColumn() == null ? "displayName" : super.getSortColumn();
    }
}
