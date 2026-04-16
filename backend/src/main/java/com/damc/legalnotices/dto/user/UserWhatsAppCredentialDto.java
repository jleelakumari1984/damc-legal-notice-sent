package com.damc.legalnotices.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWhatsAppCredentialDto {

    @NotBlank
    private String url;

    private String accessToken;

    private String attachmentDownloadUrl;

    @NotNull
    private Boolean live;

    private String testMobileNumber;
}
