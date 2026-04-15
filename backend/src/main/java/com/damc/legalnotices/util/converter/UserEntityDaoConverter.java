package com.damc.legalnotices.util.converter;

import org.springframework.stereotype.Component;

import com.damc.legalnotices.dto.user.UserResponseDto;
import com.damc.legalnotices.entity.user.UserEntity;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEntityDaoConverter {

    public UserResponseDto toDto(UserEntity e) {

        return UserResponseDto.builder()
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
