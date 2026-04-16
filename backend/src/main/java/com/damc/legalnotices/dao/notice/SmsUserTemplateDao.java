package com.damc.legalnotices.dao.notice;

import lombok.experimental.SuperBuilder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class SmsUserTemplateDao {
    private Long id;
    private Long noticeId;
    private String userTemplateContent;
    private Integer status;
    private Integer approveStatus;
    private LocalDateTime createdAt;

}
