package com.damc.legalnotices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "app.whats-app")
public class WhatsAppProperties {

    @NotNull
    private String url;
    @NotNull
    private String accessToken;
    private boolean live;
    private String testMobileNumber;
    @NotNull
    private String attachmentDownloadUrl;
}
