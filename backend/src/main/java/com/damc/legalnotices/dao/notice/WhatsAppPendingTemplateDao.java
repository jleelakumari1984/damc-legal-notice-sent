package com.damc.legalnotices.dao.notice;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WhatsAppPendingTemplateDao {
    private Long id;
    private String userName;
    private Long noticeId;
    private String noticeName;
    private String userTemplateContent;
    private LocalDateTime createdAt;

}
