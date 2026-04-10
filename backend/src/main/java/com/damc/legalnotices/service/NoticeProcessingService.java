package com.damc.legalnotices.service;

import com.damc.legalnotices.dao.ProcessedExcelDao;
import com.damc.legalnotices.dao.ProcessedNoticeItemDao;
import com.damc.legalnotices.dto.SendSampleNoticeRequestDto;

import java.util.List;

public interface NoticeProcessingService {
    List<ProcessedExcelDao> processPendingExcelParsing();

    List<ProcessedNoticeItemDao> processPendingNoticeItems();

    void sendSampleNotice(SendSampleNoticeRequestDto request);
}
