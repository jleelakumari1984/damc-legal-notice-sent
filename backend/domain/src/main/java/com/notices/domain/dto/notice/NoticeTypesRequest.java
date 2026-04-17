package com.notices.domain.dto.notice;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeTypesRequest {
    private String status;
    private Long userId;
    private String noticeName;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}