package com.notices.domain.dao.notice;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class WhatsAppUserTemplateDao {
    private Long id;
    private Long noticeId;
    private String userTemplateContent;
    private Integer status;
    private Integer approveStatus;
    private LocalDateTime createdAt;
    private Integer messageLength;
    private Integer numberOfMessage;
}
