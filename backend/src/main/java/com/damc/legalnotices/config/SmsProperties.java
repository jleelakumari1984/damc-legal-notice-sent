package com.damc.legalnotices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.sms")
public class SmsProperties {

    private String url;
    private String userName;
    private String password;
    private boolean live;
    private String testMobileNumber;
}
