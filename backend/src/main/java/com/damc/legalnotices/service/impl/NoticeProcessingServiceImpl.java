package com.damc.legalnotices.service.impl;

import com.damc.legalnotices.entity.MasterProcessSmsConfigDetail;
import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetail;
import com.damc.legalnotices.entity.ScheduledNotice;
import com.damc.legalnotices.entity.ScheduledNoticeItem;
import com.damc.legalnotices.enums.ProcessingStatus;
import com.damc.legalnotices.repository.MasterProcessSmsConfigDetailRepository;
import com.damc.legalnotices.repository.MasterProcessWhatsappConfigDetailRepository;
import com.damc.legalnotices.repository.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.ScheduledNoticeRepository;
import com.damc.legalnotices.service.NoticeProcessingService;
import com.damc.legalnotices.service.SmsSenderService;
import com.damc.legalnotices.service.WhatsappSenderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeProcessingServiceImpl implements NoticeProcessingService {

    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final MasterProcessSmsConfigDetailRepository smsConfigRepository;
    private final MasterProcessWhatsappConfigDetailRepository whatsappConfigRepository;
    private final SmsSenderService smsSenderService;
    private final WhatsappSenderService whatsappSenderService;

    @Override
    @Transactional
    public void processPendingNotices() {
        List<ScheduledNotice> notices = scheduledNoticeRepository.findTop50ByStatusOrderByCreatedAtAsc(ProcessingStatus.PENDING);
        for (ScheduledNotice notice : notices) {
            processOneNotice(notice);
        }
    }

    private void processOneNotice(ScheduledNotice notice) {
        notice.setStatus(ProcessingStatus.PROCESSING);
        scheduledNoticeRepository.save(notice);

        List<ScheduledNoticeItem> items = scheduledNoticeItemRepository.findByScheduledNoticeIdAndStatus(notice.getId(), ProcessingStatus.PENDING);
        for (ScheduledNoticeItem item : items) {
            try {
                if (Boolean.TRUE.equals(notice.getSendSms())) {
                    MasterProcessSmsConfigDetail smsConfig = smsConfigRepository
                            .findFirstByProcessIdAndStatus(notice.getProcessSno(), 1)
                            .orElseThrow(() -> new IllegalArgumentException("Active SMS config not found"));
                    smsSenderService.send(item, smsConfig, item.getPdfFilePath());
                }

                if (Boolean.TRUE.equals(notice.getSendWhatsapp())) {
                    MasterProcessWhatsappConfigDetail waConfig = whatsappConfigRepository
                            .findFirstByProcessIdAndStatus(notice.getProcessSno(), 1)
                            .orElseThrow(() -> new IllegalArgumentException("Active WhatsApp config not found"));
                    whatsappSenderService.send(item, waConfig, item.getPdfFilePath());
                }

                item.setStatus(ProcessingStatus.COMPLETED);
                item.setProcessedAt(LocalDateTime.now());
                item.setFailureReason(null);
            } catch (Exception ex) {
                log.error("Failed processing agreement {}", item.getAgreementNumber(), ex);
                item.setStatus(ProcessingStatus.FAILED);
                item.setFailureReason(ex.getMessage());
                item.setProcessedAt(LocalDateTime.now());
            }
            scheduledNoticeItemRepository.save(item);
        }

        long pendingCount = scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(notice.getId(), ProcessingStatus.PENDING);
        long failedCount = scheduledNoticeItemRepository.countByScheduledNoticeIdAndStatus(notice.getId(), ProcessingStatus.FAILED);

        notice.setProcessedAt(LocalDateTime.now());
        if (pendingCount == 0 && failedCount == 0) {
            notice.setStatus(ProcessingStatus.COMPLETED);
        } else if (pendingCount == 0) {
            notice.setStatus(ProcessingStatus.FAILED);
        } else {
            notice.setStatus(ProcessingStatus.PENDING);
        }
        scheduledNoticeRepository.save(notice);
    }
}
