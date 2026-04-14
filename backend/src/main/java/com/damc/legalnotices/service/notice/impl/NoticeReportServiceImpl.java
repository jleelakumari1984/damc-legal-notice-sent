package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;
import com.damc.legalnotices.dao.report.NoticeReportItemDao;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeEntity;
import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.ProcessingStatus;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeRepository;
import com.damc.legalnotices.service.notice.NoticeReportService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeReportServiceImpl implements NoticeReportService {

        private final ObjectMapper objectMapper;

        private final ScheduledNoticeRepository scheduledNoticeRepository;
        private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;

        @Override
        public List<NoticeReportDao> getAllNoticeReports() {
                return scheduledNoticeRepository.findAllWithProcess().stream()
                                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                                .map(this::toReportDto)
                                .toList();
        }

        @Override
        public NoticeReportDetailDao getNoticeReportDetail(Long noticeId) {
                ScheduledNoticeEntity notice = scheduledNoticeRepository.findByIdWithProcess(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));

                List<NoticeReportItemDao> items = scheduledNoticeItemRepository
                                .findByScheduledNoticeId(noticeId).stream()
                                .map(this::toItemDto)
                                .toList();

                return NoticeReportDetailDao.builder()
                                .summary(toReportDto(notice))
                                .items(items)
                                .build();
        }

        private NoticeReportDao toReportDto(ScheduledNoticeEntity e) {
                long total = scheduledNoticeItemRepository.countByScheduledNoticeId(e.getId());
                return NoticeReportDao.builder()
                                .id(e.getId())
                                .processName(e.getProcess() != null ? e.getProcess().getStepName() : null)
                                .originalFileName(e.getOriginalFileName())
                                .sendSms(e.getSendSms())
                                .sendWhatsapp(e.getSendWhatsapp())
                                .status(e.getStatus().name())
                                .createdAt(e.getCreatedAt())
                                .processedAt(e.getProcessedAt())
                                .failureReason(e.getFailureReason())
                                .totalItems(total)
                                .pendingItems(scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(e.getId(),
                                                ProcessingStatus.PENDING))
                                .processingItems(scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(
                                                e.getId(), ProcessingStatus.PROCESSING))
                                .completedItems(scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(
                                                e.getId(), ProcessingStatus.COMPLETED))
                                .failedItems(scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(e.getId(),
                                                ProcessingStatus.FAILED))
                                .build();
        }

        private NoticeReportItemDao toItemDto(ScheduledNoticeItemEntity e) {

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
}
