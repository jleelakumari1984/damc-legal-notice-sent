package com.damc.legalnotices.dao.notice;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@SuperBuilder
public class WhatsAppUserTemplateDao {
    private Long id;
    private Long processId;
    private String userTemplateContent;
    private Integer status;
    private Integer approveStatus;
    private LocalDateTime createdAt;
}
