package com.damc.legalnotices.config;

import lombok.Builder;
import lombok.Getter;

/**
 * Credential holder for WhatsApp gateway — used at send-time.
 * Built either from the global WhatsAppProperties or from a per-user DB record.
 */
@Getter
@Builder
public class WhatsAppCredential {
    private String url;
    private String accessToken;
    private String attachmentDownloadUrl;
    private boolean live;
    private String testMobileNumber;

    public static WhatsAppCredential from(WhatsAppProperties p) {
        return WhatsAppCredential.builder()
                .url(p.getUrl())
                .accessToken(p.getAccessToken())
                .attachmentDownloadUrl(p.getAttachmentDownloadUrl())
                .live(p.isLive())
                .testMobileNumber(p.getTestMobileNumber())
                .build();
    }
}
