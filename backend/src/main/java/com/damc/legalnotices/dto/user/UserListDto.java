package com.damc.legalnotices.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserListDto {
    private String search;
    private Integer accessLevel;
    private Boolean enabled;
}
