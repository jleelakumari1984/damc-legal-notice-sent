package com.notices.domain.dto.report;


import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeReportDto  {
    private Long userId;
    private String noticeName;
    private String status;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
