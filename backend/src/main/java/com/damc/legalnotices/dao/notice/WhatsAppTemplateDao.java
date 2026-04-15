package com.damc.legalnotices.dao.notice;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class WhatsAppTemplateDao extends WhatsAppUserTemplateDao {
    private Integer sentLevel;
    private String templateName;
    private String templateContent;
    private String templateLang;
}
