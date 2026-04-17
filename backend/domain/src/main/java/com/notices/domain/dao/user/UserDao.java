package com.notices.domain.dao.user;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserDao {
    private Long id;
    private String displayName;
    private String loginName;
    private String userEmail;
    private String userMobileSms;
    private String userMobileWhatsapp;
    private Long accessLevel;
    private Boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginDate;
    @Builder.Default
    private Long smsCredits = 0L;
    @Builder.Default
    private Long whatsappCredits = 0L;
    @Builder.Default
    private Long mailCredits = 0L;
}
