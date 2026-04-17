package com.damc.legalnotices.service.notice.impl;

import com.damc.legalnotices.dao.DataTableDao;
import com.damc.legalnotices.dao.notification.SendSmsDao;
import com.damc.legalnotices.dao.notification.SendWhatsappDao;
import com.damc.legalnotices.dao.report.NoticeReportDetailDao;
import com.damc.legalnotices.dao.report.NoticeReportDao;
import com.damc.legalnotices.dao.report.NoticeReportItemDao;
import com.damc.legalnotices.dao.report.NoticeReportSmsDetailsDao;
import com.damc.legalnotices.dao.report.NoticeReportWhatsappDetailsDao;
import com.damc.legalnotices.dto.DatatableDto;
import com.damc.legalnotices.dto.report.NoticeReportDto;
import com.damc.legalnotices.dto.report.NoticeSmsLogListReportDto;
import com.damc.legalnotices.dto.report.NoticeWhatsappLogListReportDto;
import com.damc.legalnotices.entity.view.ScheduleReportViewEntity;
import com.damc.legalnotices.enums.NoticeScheduleStatus;
import com.damc.legalnotices.repository.notification.SendErrorSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendErrorWhatsappDetailRepository;
import com.damc.legalnotices.repository.notification.SendLoanSmsDetailRepository;
import com.damc.legalnotices.repository.notification.SendLoanWhatsappDetailRepository;
import com.damc.legalnotices.repository.schedule.ScheduledNoticeItemRepository;
import com.damc.legalnotices.repository.view.ScheduleReportViewRepository;
import com.damc.legalnotices.dao.user.LoginUserDao;
import com.damc.legalnotices.service.notice.NoticeReportService;
import com.damc.legalnotices.util.converter.ReportEntityDaoConverter;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeReportServiceImpl implements NoticeReportService {

        private final ReportEntityDaoConverter reportEntityDaoConverter; 
        private final ScheduledNoticeItemRepository scheduledNoticeItemRepository;
        private final ScheduleReportViewRepository scheduleReportRepository;
        private final SendLoanSmsDetailRepository sendLoanSmsDetailRepository;
        private final SendErrorSmsDetailRepository sendErrorSmsDetailRepository;

        private final SendLoanWhatsappDetailRepository sendLoanWhatsappDetailRepository;
        private final SendErrorWhatsappDetailRepository sendErrorWhatsappDetailRepository;

        @Override
        public DataTableDao<List<NoticeReportDao>> getAllNoticeReports(LoginUserDao sessionUser,
                        DatatableDto<NoticeReportDto> request) {
                NoticeReportDto filter = request.getFilter();
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");
                Page<ScheduleReportViewEntity> page;
                if (filter != null) {
                        Long userId = filter.getUserId();
                        String noticeName = (filter.getNoticeName() != null && !filter.getNoticeName().isBlank())
                                        ? filter.getNoticeName().trim()
                                        : null;
                        String status = (filter.getStatus() != null && !filter.getStatus().isBlank())
                                        ? filter.getStatus().trim()
                                        : null;
                        page = scheduleReportRepository.findAllFiltered(
                                        userId, noticeName, status,
                                        filter.getFromDate(), filter.getToDate(),
                                        pageable);
                } else {
                        page = scheduleReportRepository.findAll(pageable);
                }

                return DataTableDao.<List<NoticeReportDao>>builder()
                                .draw(request.getDraw())
                                .recordsTotal(page.getTotalElements())
                                .recordsFiltered(page.getContent().size())
                                .data(page.getContent().stream()
                                                .map(reportEntityDaoConverter::toReportDto)
                                                .toList())
                                .build();
        }

       
        @Override
        public NoticeReportDetailDao getNoticeReportDetail(LoginUserDao sessionUser, Long noticeId, String status) {
                ScheduleReportViewEntity notice = scheduleReportRepository.findById(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));

                List<NoticeReportItemDao> items;
                if (status != null && !status.isBlank()) {
                        NoticeScheduleStatus processingStatus = NoticeScheduleStatus.valueOf(status.toUpperCase());
                        if (processingStatus == null) {
                                throw new IllegalArgumentException("Invalid status value: " + status);
                        }
                        items = scheduledNoticeItemRepository
                                        .findByScheduledNoticeIdAndStatus(noticeId, processingStatus).stream()
                                        .map(reportEntityDaoConverter::toReportItemDto)
                                        .toList();
                } else {
                        items = scheduledNoticeItemRepository
                                        .findByScheduledNoticeId(noticeId).stream()
                                        .map(reportEntityDaoConverter::toReportItemDto)
                                        .toList();
                }

                return NoticeReportDetailDao.builder()
                                .summary(reportEntityDaoConverter.toReportDto(notice))
                                .items(items)
                                .build();
        }

        @Override
        public DataTableDao<NoticeReportSmsDetailsDao> getSmsLogs(LoginUserDao sessionUser, Long noticeId,
                        DatatableDto<NoticeSmsLogListReportDto> request) {
                ScheduleReportViewEntity notice = scheduleReportRepository.findById(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");

                Page<SendSmsDao> page;
                String status = request.getFilter() != null ? request.getFilter().getStatus() : null;
                if (status != null && !status.isBlank()) {
                        page = sendLoanSmsDetailRepository
                                        .findByScheduleSnoAndReceivedStatus(noticeId, status, pageable)
                                        .map(reportEntityDaoConverter::toSmsLogDao);
                } else {
                        page = sendLoanSmsDetailRepository
                                        .findByScheduleSno(noticeId, pageable)
                                        .map(reportEntityDaoConverter::toSmsLogDao);
                }

                return DataTableDao.<NoticeReportSmsDetailsDao>builder()
                                .draw(request.getDraw())
                                .recordsTotal(page.getTotalElements())
                                .recordsFiltered(page.getContent().size())
                                .data(NoticeReportSmsDetailsDao.builder()
                                                .summary(reportEntityDaoConverter.toReportDto(notice))
                                                .items(page.getContent())
                                                .build())
                                .build();
        }

        @Override
        public DataTableDao<NoticeReportSmsDetailsDao> getSmsErrorLogs(LoginUserDao sessionUser, Long noticeId,
                        DatatableDto<NoticeSmsLogListReportDto> request) {
                ScheduleReportViewEntity notice = scheduleReportRepository.findById(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");

                Page<SendSmsDao> page = sendErrorSmsDetailRepository
                                .findByScheduleSno(noticeId, pageable)
                                .map(reportEntityDaoConverter::toSmsLogDao);

                return DataTableDao.<NoticeReportSmsDetailsDao>builder()
                                .draw(request.getDraw())
                                .recordsTotal(page.getTotalElements())
                                .recordsFiltered(page.getContent().size())
                                .data(NoticeReportSmsDetailsDao.builder()
                                                .summary(reportEntityDaoConverter.toReportDto(notice))
                                                .items(page.getContent())
                                                .build())
                                .build();
        }

        @Override
        public DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppLogs(LoginUserDao sessionUser, Long noticeId,
                        DatatableDto<NoticeWhatsappLogListReportDto> request) {
                ScheduleReportViewEntity notice = scheduleReportRepository.findById(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");

                Page<SendWhatsappDao> page;
                String status = request.getFilter() != null ? request.getFilter().getStatus() : null;
                if (status != null && !status.isBlank()) {
                        page = sendLoanWhatsappDetailRepository
                                        .findByScheduleSnoAndReceivedStatus(noticeId, status, pageable)
                                        .map(reportEntityDaoConverter::toWhatsAppLogDao);
                } else {
                        page = sendLoanWhatsappDetailRepository
                                        .findByScheduleSno(noticeId, pageable)
                                        .map(reportEntityDaoConverter::toWhatsAppLogDao);
                }

                return DataTableDao.<NoticeReportWhatsappDetailsDao>builder()
                                .draw(request.getDraw())
                                .recordsTotal(page.getTotalElements())
                                .recordsFiltered(page.getContent().size())
                                .data(NoticeReportWhatsappDetailsDao.builder()
                                                .summary(reportEntityDaoConverter.toReportDto(notice))
                                                .items(page.getContent())
                                                .build())
                                .build();
        }

        @Override
        public DataTableDao<NoticeReportWhatsappDetailsDao> getWhatsAppErrorLogs(LoginUserDao sessionUser,
                        Long noticeId,
                        DatatableDto<NoticeWhatsappLogListReportDto> request) {
                ScheduleReportViewEntity notice = scheduleReportRepository.findById(noticeId)
                                .orElseThrow(() -> new IllegalArgumentException("Notice not found: " + noticeId));
                Pageable pageable = request == null || request.isAllData() ? Pageable.unpaged()
                                : request.getPagination("id");

                Page<SendWhatsappDao> page = sendErrorWhatsappDetailRepository
                                .findByScheduleSno(noticeId, pageable)
                                .map(reportEntityDaoConverter::toWhatsAppLogDao);

                return DataTableDao.<NoticeReportWhatsappDetailsDao>builder()
                                .draw(request.getDraw())
                                .recordsTotal(page.getTotalElements())
                                .recordsFiltered(page.getContent().size())
                                .data(NoticeReportWhatsappDetailsDao.builder()
                                                .summary(reportEntityDaoConverter.toReportDto(notice))
                                                .items(page.getContent())
                                                .build())
                                .build();
        }

}
