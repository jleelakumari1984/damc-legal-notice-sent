package com.notices.domain.util.converter;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.notices.domain.dao.notification.SendSmsDao;
import com.notices.domain.dao.notification.SendWhatsappDao;
import com.notices.domain.dao.report.NoticeReportDao;
import com.notices.domain.dao.report.NoticeReportItemDao;
import com.notices.domain.entity.notification.SendErrorSmsDetailEntity;
import com.notices.domain.entity.notification.SendErrorWhatsappDetailEntity;
import com.notices.domain.entity.notification.SendLoanSmsDetailEntity;
import com.notices.domain.entity.notification.SendLoanWhatsappDetailEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeItemEntity;
import com.notices.domain.entity.view.ScheduleReportViewEntity;
import com.notices.domain.util.TemplateUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReportEntityDaoConverter {
    private final ObjectMapper objectMapper;
    private final TemplateUtil templateUtil;



    public NoticeReportDao toReportDto(ScheduleReportViewEntity e) {
        return NoticeReportDao.builder()
                .id(e.getId())
                .noticeName(e.getTemplateStepName())
                .createdUserName(e.getCreatedUserName())
                .originalFileName(e.getOriginalFileName())
                .sendSms(e.getSendSms())
                .sendWhatsapp(e.getSendWhatsapp())
                .status(e.getStatus())
                .createdAt(e.getCreatedAt())
                .noticeedAt(e.getProcessedAt())
                .failureReason(e.getFailureReason())
                .totalItems(e.getTotalLoans())
                .pendingItems(e.getPendingLoans())
                .pendingItems(e.getProcessingLoans())
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
        String text = templateUtil.getSmsLogMessage(e.getMessage());

        return SendSmsDao.builder()
                .sendTo(e.getSendTo())
                .message(text)
                .sendAt(e.getSendAt())
                .sendStatus(-1)
                .receivedStatus("ERROR")
                .errorMessage(e.getErrorMessage())
                .build();
    }

    public SendSmsDao toSmsLogDao(SendLoanSmsDetailEntity e) {
        String text = templateUtil.getSmsLogMessage(e.getMessage());

        return SendSmsDao.builder()
                .sendTo(e.getSendTo())
                .message(text)
                .sendAt(e.getSendAt())
                .sendStatus(e.getSendStatus())
                .sendResponse(e.getSendResponse())
                .ackId(e.getAckId())
                .receivedStatus(e.getReceivedStatus())
                .receivedAt(e.getReceivedAt())
                .errorMessage(e.getErrorMessage())
                .build();
    }

    public SendWhatsappDao toWhatsAppLogDao(SendLoanWhatsappDetailEntity e) {
        String text = templateUtil.getWhatsAppLogMessage(e.getMessage());

        return SendWhatsappDao.builder()
                .sendTo(e.getSendTo())
                .message(text)
                .sendAt(e.getSendAt())
                .sendStatus(e.getSendStatus())
                .sendResponse(e.getSendResponse())
                .ackId(e.getAckId())
                .receivedStatus(e.getReceivedStatus())
                .receivedAt(e.getReceivedAt())
                .build();
    }

    public SendWhatsappDao toWhatsAppLogDao(SendErrorWhatsappDetailEntity e) {
        String text = templateUtil.getWhatsAppLogMessage(e.getMessage());
        return SendWhatsappDao.builder()
                .sendTo(e.getSendTo())
                .message(text)
                .sendAt(e.getSendAt())
                .sendStatus(-1)
                .receivedStatus("ERROR")
                .errorMessage(e.getErrorMessage())
                .build();
    }
}
