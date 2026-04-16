package com.damc.legalnotices.util.validator;

import com.damc.legalnotices.dto.notice.NoticeWhatsappConfigDto;
import com.damc.legalnotices.errors.InvalidInputException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NoticeWhatsappMappingValidationUtil {

    private NoticeWhatsappMappingValidationUtil() {
    }

    public static void validateAdminTemplate(NoticeWhatsappConfigDto request) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (request.getTemplateContent() == null || request.getTemplateContent().isBlank())
            errorMap.computeIfAbsent("templateContent", k -> new ArrayList<>()).add("Template content is required");
        if (request.getTemplateName() == null || request.getTemplateName().isBlank())
            errorMap.computeIfAbsent("templateName", k -> new ArrayList<>()).add("Template name is required");
        if (request.getTemplateLang() == null || request.getTemplateLang().isBlank())
            errorMap.computeIfAbsent("templateLang", k -> new ArrayList<>()).add("Template language is required");
        if (!errorMap.isEmpty())
            throw new InvalidInputException("Invalid WhatsApp admin template config", errorMap);
    }

    public static void validateUserTemplate(NoticeWhatsappConfigDto request) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (request.getUserTemplateContent() == null || request.getUserTemplateContent().isBlank())
            errorMap.computeIfAbsent("userTemplateContent", k -> new ArrayList<>())
                    .add("User template content is required");
        if (!errorMap.isEmpty())
            throw new InvalidInputException("Invalid WhatsApp user template config", errorMap);
    }
}
