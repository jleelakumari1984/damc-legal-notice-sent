package com.damc.legalnotices.dao.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSmsCredentialDao {
    private String url;
    private String userName;
    private String password;
    private Boolean live;
    private String testMobileNumber;
}
