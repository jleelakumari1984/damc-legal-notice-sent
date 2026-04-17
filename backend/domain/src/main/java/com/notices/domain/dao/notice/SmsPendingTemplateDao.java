package com.notices.domain.dao.notice;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SmsPendingTemplateDao {
    private Long id;
    private String userName;
    private Long noticeId;
    private String noticeName;
    private String userTemplateContent;
    private String templateContent;
    private Integer messageLength;
    private Integer numberOfMessage;
    private LocalDateTime createdAt;
}
