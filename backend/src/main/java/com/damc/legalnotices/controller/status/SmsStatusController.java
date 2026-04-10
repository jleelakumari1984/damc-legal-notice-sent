package com.damc.legalnotices.controller.status;

import com.damc.legalnotices.entity.StatusReportSmsEntity;
import com.damc.legalnotices.service.NotificationCallbackService;
import com.damc.legalnotices.service.SmsStatusService;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/status-report/sms")
@RequiredArgsConstructor
public class SmsStatusController {

    private final SmsStatusService smsStatusService;
    private final NotificationCallbackService callbackService;

    @GetMapping(path = { "/callback", "/callback/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> status(
            final HttpServletRequest request,
            @RequestParam(required = false) Map<String, String> requestParams) {
        return postStatus(request, null, requestParams);
    }

    @PostMapping(path = { "/callback",
            "/callback/" }, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Map<String, String>> postStatus(
            final HttpServletRequest request,
            @RequestBody String requestBody,
            @RequestParam(required = false) Map<String, String> requestParams) {
        Map<String, String> result = new HashMap<>();
        StatusReportSmsEntity entity = null;
        try {
            String queryString = "";
            if (requestParams != null && !requestParams.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                queryString = objectMapper.writeValueAsString(requestParams);
            }
            if (!StringUtils.hasText(requestBody)) {
                requestBody = queryString;
            }
            if (StringUtils.hasText(queryString) || StringUtils.hasText(requestBody)) {
                result.put("status", "success");
                entity = smsStatusService.saveData(queryString, requestBody);
                callbackService.processSmsDeliveryReport(queryString);
                entity.setCompleteDate(LocalDateTime.now());
                entity.setProcessDate(LocalDateTime.now());
                entity.setStatus("complete");
                smsStatusService.saveData(entity);
            } else {
                result.put("status", "no data");
                log.warn("SmsStatusController: Request Data is Empty");
            }
        } catch (Exception e) {
            result.put("status", "error");
            log.error("SmsStatusController POST error: {}", e.getMessage());
            if (entity != null) {
                entity.setCompleteDate(LocalDateTime.now());
                entity.setStatus("error");
                entity.setDescription(e.getMessage());
                smsStatusService.saveData(entity);
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
