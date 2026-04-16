package com.damc.legalnotices.dao.notice;

import lombok.experimental.SuperBuilder;
import lombok.Getter;

@Getter
@SuperBuilder
public class SmsTemplateDao extends SmsUserTemplateDao {
    private String peid;
    private String senderId;
    private String routeId;
    private String templateContent;
    private String templateId;
    private String channel;
    private Integer dcs;
    private Integer flashSms;
    private String createdUserName;
    private boolean ownTemplate;
}
