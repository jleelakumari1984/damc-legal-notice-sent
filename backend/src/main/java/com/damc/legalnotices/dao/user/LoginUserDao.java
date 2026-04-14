package com.damc.legalnotices.dao.user;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUserDao {
    private Long id;
    @JsonIgnore
    private String password;
    private String displayName;
    private String loginName;
    private String userEmail;
    private String mobileSms;
    private String mobileWhatsapp;
    private Long accessLevel;
}
