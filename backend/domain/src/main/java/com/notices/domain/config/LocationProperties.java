package com.notices.domain.config;

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

    @NotNull
    private String endPoints;

    private int poiMaxByteArrayOverride = Integer.MAX_VALUE;

    private int previewMaxRows = 100;

}
