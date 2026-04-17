package com.notices.domain.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeSmsConfigDto {

    @NotNull(message = "noticeId is required")
    private Long noticeId;

    private String peid;
    private String senderId;
    private String routeId;
    private String templateContent;
    private String userTemplateContent;
    private String templateId;
    private String channel;
    private Integer dcs;
    private Integer flashSms;
    private Integer status;
}
