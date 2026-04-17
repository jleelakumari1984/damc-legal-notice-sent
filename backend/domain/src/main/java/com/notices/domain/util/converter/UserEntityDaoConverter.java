package com.notices.domain.util.converter;

import org.springframework.stereotype.Component;

import com.notices.domain.dao.user.UserDao;
import com.notices.domain.entity.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEntityDaoConverter {

    public UserDao toDao(UserEntity e) {

        return UserDao.builder()
                .id(e.getId())
                .displayName(e.getDisplayName())
                .loginName(e.getLoginName())
                .userEmail(e.getUserEmail())
                .userMobileSms(e.getUserMobileSms())
                .userMobileWhatsapp(e.getUserMobileWhatsapp())
                .accessLevel(e.getAccessLevel())
                .enabled(e.getEnabled())
                .createdAt(e.getCreatedAt())
                .lastLoginDate(e.getLastLoginDate())
                .smsCredits(e.getCredits() != null ? e.getCredits().getSmsCredits() : 0L)
                .whatsappCredits(e.getCredits() != null ? e.getCredits().getWhatsappCredits() : 0L)
                .mailCredits(e.getCredits() != null ? e.getCredits().getMailCredits() : 0L)
                .build();
    }
}
