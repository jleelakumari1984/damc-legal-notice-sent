package com.damc.legalnotices.controller;

import com.damc.legalnotices.service.WhatsAppStatusService;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/status-report/whatsapp")
@RequiredArgsConstructor
public class WhatsAppStatusController {

    private final WhatsAppStatusService whatsAppStatusService;

    @GetMapping(path = { "/callback", "/callback/" }, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> status(
            final HttpServletRequest request,
            @RequestParam(required = false) Map<String, String> requestParams) {
        Map<String, String> result = new HashMap<>();
        try {
            String queryString = request.getQueryString();
            String reportData = "";
            if (requestParams != null && !requestParams.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                reportData = objectMapper.writeValueAsString(requestParams);
            }
            if (StringUtils.hasText(queryString) || StringUtils.hasText(reportData)) {
                result.put("status", "success");
                whatsAppStatusService.saveData(queryString, reportData);
            } else {
                result.put("status", "no data");
                log.warn("WhatsAppStatusController: Request Data is Empty");
            }
        } catch (Exception e) {
            result.put("status", "error");
            log.error("WhatsAppStatusController GET error: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PostMapping(path = { "/callback",
            "/callback/" }, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public ResponseEntity<Map<String, String>> postStatus(
            final HttpServletRequest request,
            @RequestBody String requestBody,
            @RequestParam(required = false) Map<String, String> requestParams) {
        Map<String, String> result = new HashMap<>();
        try {
            String reportData = "";
            if (requestParams != null && !requestParams.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
                reportData = objectMapper.writeValueAsString(requestParams);
            }
            if (StringUtils.hasText(reportData) || StringUtils.hasText(requestBody)) {
                result.put("status", "success");
                whatsAppStatusService.saveData(reportData, requestBody);
            } else {
                result.put("status", "no data");
                log.warn("WhatsAppStatusController: Request Data is Empty");
            }
        } catch (Exception e) {
            result.put("status", "error");
            log.error("WhatsAppStatusController POST error: {}", e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
