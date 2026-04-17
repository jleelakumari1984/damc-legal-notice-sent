package com.notices;

import com.notices.domain.config.LocationProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.poi.util.IOUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
@RequiredArgsConstructor
public class LegalNoticesApplication {

    private final LocationProperties locationProperties;

    public static void main(String[] args) {
        SpringApplication.run(LegalNoticesApplication.class, args);
    }

    @PostConstruct
    public void init() {
        IOUtils.setByteArrayMaxOverride(locationProperties.getPoiMaxByteArrayOverride());
    }
}
