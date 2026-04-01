package com.damc.legalnotices.dto;

import com.damc.legalnotices.dao.LoginUserDao;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponseDto {
    private String accessToken;
    private LoginUserDao user;
}
