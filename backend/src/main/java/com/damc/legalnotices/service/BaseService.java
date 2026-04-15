package com.damc.legalnotices.service;

import com.damc.legalnotices.dao.user.LoginUserDao; 

public interface BaseService {
    LoginUserDao getSessionUser();
}
