package com.damc.legalnotices.util;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.damc.legalnotices.dao.notification.SendSmsDao;
import com.damc.legalnotices.dao.notification.SendWhatsappDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;
import com.damc.legalnotices.dao.report.NoticeReportItemDao;
import com.damc.legalnotices.entity.notification.SendErrorSmsDetailEntity;
import com.damc.legalnotices.entity.notification.SendErrorWhatsappDetailEntity;
import com.damc.legalnotices.entity.notification.SendLoanSmsDetailEntity;
import com.damc.legalnotices.entity.notification.SendLoanWhatsappDetailEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.damc.legalnotices.entity.view.ScheduleReportViewEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEntityDaoConverter {
    private final ObjectMapper objectMapper;

    public NoticeReportDao toReportDto(ScheduleReportViewEntity e) {
        return NoticeReportDao.builder()
                .id(e.getId())
                .processName(e.getTemplateStepName())
                .originalFileName(e.getOriginalFileName())
                .sendSms(e.getSendSms())
                .sendWhatsapp(e.getSendWhatsapp())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .processedAt(e.getProcessedAt())
                .failureReason(e.getFailureReason())
                .totalItems(e.getTotalLoans())
                .pendingItems(e.getPendingLoans())
                .processingItems(e.getProcessingLoans())
                .completedItems(e.getCompletedLoans())
                .failedItems(e.getFailedLoans())
                .build();
    }

    public NoticeReportItemDao toReportItemDto(ScheduledNoticeItemEntity e) {

        Map<String, Object> excelDataMap = null;
        if (e.getExcelData() != null && !e.getExcelData().isBlank()) {
            try {
                excelDataMap = objectMapper.readValue(e.getExcelData(), new TypeReference<>() {
                });
            } catch (Exception ex) {
                log.warn("Failed to parse excelData for item id={}", e.getId(), ex);
            }
        }
        return NoticeReportItemDao.builder()
                .id(e.getId())
                .excelData(excelDataMap)
                .agreementNumber(e.getAgreementNumber())
                .status(e.getStatus().name())
                .failureReason(e.getFailureReason())
                .processedAt(e.getProcessedAt())
                .build();
    }

    public SendSmsDao toSmsLogDao(SendErrorSmsDetailEntity e) {
        return SendSmsDao.builder()
                .sendTo(e.getSendTo())
                .message(e.getMessage())
                .sendAt(e.getSendAt())
                .sendStatus(-1)
                .receivedStatus("ERROR")
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    public SendSmsDao toSmsLogDao(SendLoanSmsDetailEntity e) {
        return SendSmsDao.builder()
                .sendTo(e.getSendTo())
                .message(e.getMessage())
                .sendAt(e.getSendAt())
                .sendStatus(e.getSendStatus())
                .sendResponse(e.getSendResponse())
                .ackId(e.getAckId())
                .receivedStatus(e.getReceivedStatus())
                .receivedAt(e.getReceivedAt())
                .createdBy(e.getCreatedBy())
                .createdAt(e.getCreatedAt())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    public SendWhatsappDao toWhatsAppLogDao(SendLoanWhatsappDetailEntity e) {
        return SendWhatsappDao.builder()
                .sendTo(e.getSendTo())
                .message(e.getMessage())
                .sendAt(e.getSendAt())
                .sendStatus(e.getSendStatus())
                .sendResponse(e.getSendResponse())
                .ackId(e.getAckId())
                .receivedStatus(e.getReceivedStatus())
                .receivedAt(e.getReceivedAt())
                .build();
    }

    public SendWhatsappDao toWhatsAppLogDao(SendErrorWhatsappDetailEntity e) {
        return SendWhatsappDao.builder()
                .sendTo(e.getSendTo())
                .message(e.getMessage())
                .sendAt(e.getSendAt())
                .sendStatus(-1)
                .receivedStatus("ERROR")
                .errorMessage(e.getErrorMessage())
                .build();
    }
}
