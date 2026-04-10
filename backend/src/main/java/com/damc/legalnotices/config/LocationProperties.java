package com.damc.legalnotices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "app.location")
public class LocationProperties {

    @NotNull
    private String uploadDir;

    @NotNull
    private String templateLocation;

}
