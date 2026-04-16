package com.damc.legalnotices.dao.notice;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WhatsAppTemplateDao extends WhatsAppUserTemplateDao {
    private String templateName;
    private String templateContent;
    private String templateLang;
    private String createdUserName;
    private boolean ownTemplate;
}
