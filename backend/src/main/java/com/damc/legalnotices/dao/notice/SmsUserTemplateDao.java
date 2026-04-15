package com.damc.legalnotices.dao.notice;

import lombok.experimental.SuperBuilder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class SmsUserTemplateDao {
    private Long id;
    private Long processId;
    private String userTemplateText;
    private Integer status;
    private LocalDateTime createdAt;
}
