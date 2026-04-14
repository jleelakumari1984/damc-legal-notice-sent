package com.damc.legalnotices.dao.notice;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SmsTemplateDao {
    private Long id;
    private Long processId;
    private Long hearingStageId;
    private String hearingStageTitle;
    private Integer sentLevel;
    private String peid;
    private String senderId;
    private String routeId;
    private String templateText;
    private String templateId;
    private String channel;
    private Integer dcs;
    private Integer flashSms;
    private Integer status;
    private LocalDateTime createdAt;
}
