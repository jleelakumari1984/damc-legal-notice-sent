package com.damc.legalnotices.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.service.BaseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BaseServiceImpl implements BaseService {
    @Override
    public LoginUserDao getSessionUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object sessPrincipal = authentication.getPrincipal();
            if (sessPrincipal instanceof LoginUserDao) {
                return (LoginUserDao) sessPrincipal;
            }
        }
        throw new IllegalArgumentException("Invalid session user");
    }

}
