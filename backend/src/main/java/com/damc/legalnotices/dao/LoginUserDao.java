package com.damc.legalnotices.dao;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginUserDao {
    private Long id;
    private String displayName;
    private String loginName;
    private String userEmail;
    private String mobileSms;
    private String mobileWhatsapp;
    private Long accessLevel;
}
