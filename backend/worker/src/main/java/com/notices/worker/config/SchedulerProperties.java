package com.notices.worker.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.scheduler")
public class SchedulerProperties {

    private int batchSize;
    
    private long fixedDelayMs;
}
