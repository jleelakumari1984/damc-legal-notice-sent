package com.notices.domain.service.schedule.impl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notices.domain.dao.excel.ExcelPreviewDao;
import com.notices.domain.dao.excel.ExcelPreviewRowDao;
import com.notices.domain.dao.notice.NoticeExcelMappingDao;
import com.notices.domain.dao.schedule.ScheduledNoticeDao;
import com.notices.domain.dao.user.LoginUserDao;
import com.notices.domain.dto.notice.NoticeValidationFileDto;
import com.notices.domain.dto.notice.NoticeValidationRowDto;
import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeEntity;
import com.notices.domain.entity.schedule.ScheduledNoticeItemEntity;
import com.notices.domain.enums.NoticeScheduleStatus;
import com.notices.domain.repository.schedule.ScheduledNoticeItemRepository;
import com.notices.domain.repository.schedule.ScheduledNoticeRepository;
import com.notices.domain.service.schedule.SaveScheduleService;
import com.notices.domain.util.ExcelParserUtil;
import com.notices.domain.util.converter.EntityDaoConverter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaveScheduleServiceImpl implements SaveScheduleService {
    private final ObjectMapper objectMapper;

    private final ScheduledNoticeRepository scheduledNoticeRepository;
    private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
    private final ExcelParserUtil excelParserUtil;
    private final EntityDaoConverter entityDaoConverter;

     
    @Override
    public NoticeValidationFileDto storeExcelData(Path extractedPath, Path directExcelPath,
            MasterProcessTemplateDetailEntity template, ScheduledNoticeEntity scheduledNotice) {
        NoticeExcelMappingDao keyColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsAgreement() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .findFirst()
                .orElse(null);
        NoticeExcelMappingDao customerNameColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsCustomerName() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .findFirst()
                .orElse(null);
        List<NoticeExcelMappingDao> attachmentColumns = template.getExcelMappings().stream()
                .filter(mapping -> mapping.getIsAttachment() == 1)
                .map(entityDaoConverter::toNoticeExcelMappingDao)
                .toList();
        Path excelPath = directExcelPath != null ? directExcelPath : excelParserUtil.findExcel(extractedPath);
        ExcelPreviewDao excelPreview;
        try {
            excelPreview = excelParserUtil.parseAsPreview(excelPath);
        } catch (IOException ex) {
            throw new IllegalArgumentException("Unable to parse excel from ZIP: " + ex.getMessage());
        }
        if (excelPreview.getRows().isEmpty()) {
            throw new IllegalArgumentException("Excel has no valid agreement rows");
        }
        List<NoticeValidationRowDto> validationRows = new ArrayList<>();
        for (ExcelPreviewRowDao row : excelPreview.getRows()) {
            String agreementNumber = row.getKeyColumnData(List.of(keyColumns), null);
            String customerName = row.getKeyColumnData(List.of(customerNameColumns), null);
            String excelData;
            try {
                excelData = objectMapper.writeValueAsString(row.getData());
            } catch (Exception ex) {
                excelData = row.toString();
            }

            ScheduledNoticeItemEntity item = new ScheduledNoticeItemEntity();
            item.setScheduledNotice(scheduledNotice);
            item.setAgreementNumber(agreementNumber);
            item.setCustomerName(customerName);
            item.setAttachments(row.getKeyColumnData(attachmentColumns, ";"));
            item.setExcelData(excelData);
            item.setStatus(NoticeScheduleStatus.UPLOADED);
            scheduledNoticeItemRepository.save(item);

            validationRows.add(NoticeValidationRowDto.builder()
                    .agreementNumber(agreementNumber)
                    .customerName(customerName)
                    .excelData(excelData)
                    .build());
        }
        return NoticeValidationFileDto.builder()
                .columnNames(excelPreview.getColumnNames())
                .rows(validationRows)
                .build();
    }

    @Transactional
    @Override
    public ScheduledNoticeDao stopSchedule(LoginUserDao sessionUser, Long noticeId) {
        ScheduledNoticeEntity notice = scheduledNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + noticeId));
        if (notice.getStatus() == null || NoticeScheduleStatus.EXCELUPLOADED == notice.getStatus()
                || NoticeScheduleStatus.STOP == notice.getStatus()) {
            throw new IllegalStateException("Schedule is already stopped: " + noticeId);
        }
        scheduledNoticeItemRepository.updateStopStatus(noticeId, NoticeScheduleStatus.STOP.name());
        notice.setStatus(NoticeScheduleStatus.STOP);
        notice = scheduledNoticeRepository.save(notice);

        return entityDaoConverter.toScheduledNoticeDao(notice);
    }

    @Transactional
    @Override
    public ScheduledNoticeDao startSchedule(LoginUserDao sessionUser, Long noticeId) {
        ScheduledNoticeEntity notice = scheduledNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("Report not found: " + noticeId));

        if (notice.getStatus() == null
                || NoticeScheduleStatus.START == notice.getStatus()) {
            throw new IllegalStateException("Schedule is already started: " + noticeId);
        }
        scheduledNoticeItemRepository.updateStartStatus(noticeId, NoticeScheduleStatus.PENDING.name());
        notice.setStatus(NoticeScheduleStatus.START);
        notice = scheduledNoticeRepository.save(notice);
        return entityDaoConverter.toScheduledNoticeDao(notice);
    }

}
