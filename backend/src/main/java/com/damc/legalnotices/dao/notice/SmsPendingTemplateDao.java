package com.damc.legalnotices.dao.notice;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SmsPendingTemplateDao {
    private Long id;
    private String userName;
    private String processName;
    private String userTemplateContent;
    private LocalDateTime createdAt;
}
