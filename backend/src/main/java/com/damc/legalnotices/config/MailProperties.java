package com.damc.legalnotices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.mail")
public class MailProperties {

    private boolean live;
    private String fromName;
    private String bcc;
    private String testMailId;
    private String errorTo;
    private String errorBcc;
}
