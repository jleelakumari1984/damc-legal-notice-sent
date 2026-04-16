package com.damc.legalnotices.dto.notice;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeSmsApproveDto {

    @NotNull
    private String peid;

    @NotNull
    private String senderId;

    @NotNull
    private String routeId;

    @NotNull
    private String templateId;

    @NotNull
    private String channel;

    private Integer dcs;
    private Integer flashSms;
 
}
