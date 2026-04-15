package com.damc.legalnotices.service.user;

import com.damc.legalnotices.dao.user.LoginDao;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.dto.user.LoginDto;
import com.damc.legalnotices.dto.user.SwitchSessionDto;

public interface AuthService {
    LoginDao login(LoginDto requestDto);
    LoginDao switchSession(LoginUserDao sessionUser, SwitchSessionDto request);
}
