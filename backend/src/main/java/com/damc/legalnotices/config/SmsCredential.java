package com.damc.legalnotices.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Credential holder for SMS gateway — used at send-time.
 * Built either from the global SmsProperties or from a per-user DB record.
 */
@Getter
@Builder
public class SmsCredential {
    private String url;
    private String userName;
    private String password;
    private boolean live;
    private String testMobileNumber;

    public static SmsCredential from(SmsProperties p) {
        return SmsCredential.builder()
                .url(p.getUrl())
                .userName(p.getUserName())
                .password(p.getPassword())
                .live(p.isLive())
                .testMobileNumber(p.getTestMobileNumber())
                .build();
    }
}
