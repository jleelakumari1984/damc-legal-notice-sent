package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.user.LoginDao;
import com.damc.legalnotices.dto.user.LoginDto;

public interface AuthService {
    LoginDao login(LoginDto requestDto);
}
