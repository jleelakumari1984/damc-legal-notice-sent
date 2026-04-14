package com.damc.legalnotices.service;

import com.damc.legalnotices.dao.user.SessionUserDao;

public interface BaseService {
    SessionUserDao getSessionUser();
}
