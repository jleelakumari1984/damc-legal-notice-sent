package com.notices.domain.dao.notification;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StatusReportSmsDao {
    private Long id;
    private String requestParams;
    private String requestBody;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime noticeDate;
    private LocalDateTime completeDate;
    private String description;
}
