package com.damc.legalnotices.dao.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserCreditDao {
    private Long id;
    private Long userId;
    private String displayName;
    private Long smsCredits;
    private Long whatsappCredits;
    private Long mailCredits;
}
