package com.notices.api.util.validator;

import com.notices.api.errors.InvalidInputException;
import com.notices.domain.dto.notice.NoticeSmsConfigDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NoticeSmsMappingValidationUtil {

    private NoticeSmsMappingValidationUtil() {
    }

    public static void validateAdminTemplate(NoticeSmsConfigDto request) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (request.getTemplateContent() == null || request.getTemplateContent().isBlank())
            errorMap.computeIfAbsent("templateContent", k -> new ArrayList<>()).add("Template content is required");
        if (request.getPeid() == null || request.getPeid().isBlank())
            errorMap.computeIfAbsent("peid", k -> new ArrayList<>()).add("PEID (principal entity ID) is required");
        if (request.getSenderId() == null || request.getSenderId().isBlank())
            errorMap.computeIfAbsent("senderId", k -> new ArrayList<>()).add("Sender ID is required");
        if (request.getRouteId() == null || request.getRouteId().isBlank())
            errorMap.computeIfAbsent("routeId", k -> new ArrayList<>()).add("Route ID is required");
        if (request.getTemplateId() == null || request.getTemplateId().isBlank())
            errorMap.computeIfAbsent("templateId", k -> new ArrayList<>()).add("Template ID is required");
        if (request.getChannel() == null || request.getChannel().isBlank())
            errorMap.computeIfAbsent("channel", k -> new ArrayList<>()).add("Channel is required");
        if (!errorMap.isEmpty())
            throw new InvalidInputException("Invalid SMS admin template config", errorMap);
    }

    public static void validateUserTemplate(NoticeSmsConfigDto request) {
        Map<String, List<String>> errorMap = new HashMap<>();
        if (request.getUserTemplateContent() == null || request.getUserTemplateContent().isBlank())
            errorMap.computeIfAbsent("userTemplateContent", k -> new ArrayList<>())
                    .add("User template content is required");
        if (!errorMap.isEmpty())
            throw new InvalidInputException("Invalid SMS user template config", errorMap);
    }
}
