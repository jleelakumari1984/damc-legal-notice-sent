package com.notices.domain.dao.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginDao {
    private String accessToken;
    private LoginUserDao user;
}
