package com.notices.api.service;

import com.notices.domain.dao.user.LoginUserDao; 

public interface BaseService {
    LoginUserDao getSessionUser();
}
