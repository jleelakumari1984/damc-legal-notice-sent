package com.notices.domain.dao.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserWhatsAppCredentialDao {
    private String url;
    private String accessToken;
    private String attachmentDownloadUrl;
    private Boolean live;
    private String testMobileNumber;
}
