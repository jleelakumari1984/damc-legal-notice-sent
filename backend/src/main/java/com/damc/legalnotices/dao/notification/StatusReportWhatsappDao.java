package com.damc.legalnotices.dao.notification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StatusReportWhatsappDao {
    private Long id;
    private String requestParams;
    private String requestBody;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime processDate;
    private LocalDateTime completeDate;
    private String description;
}
