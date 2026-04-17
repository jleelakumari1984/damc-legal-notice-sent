package com.notices.api.service.user;

import com.notices.domain.dao.user.LoginDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.user.LoginDto;
import com.notices.domain.dto.user.SwitchSessionDto;

public interface AuthService {
    LoginDao login(LoginDto requestDto);
    LoginDao switchSession(LoginUserDao sessionUser, SwitchSessionDto request);
}
