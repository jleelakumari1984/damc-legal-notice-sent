package com.damc.legalnotices.dao.report;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

import com.damc.legalnotices.dao.notification.SendWhatsappDao;

@Getter
@Builder
public class NoticeReportWhatsappDetailsDao {
    private NoticeReportDao summary;
    private List<SendWhatsappDao> items;
    
}
