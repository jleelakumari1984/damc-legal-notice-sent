package com.damc.legalnotices.service.report;

import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;
import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;

import java.util.List;

public interface StatusReportService {

    StatusReportSmsEntity saveSmsReport(StatusReportSmsEntity entity);

    StatusReportWhatsappEntity saveWhatsappReport(StatusReportWhatsappEntity entity);

    List<StatusReportSmsEntity> getAllSmsReports();

    List<StatusReportWhatsappEntity> getAllWhatsappReports();

    StatusReportSmsEntity getSmsReport(Long id);

    StatusReportWhatsappEntity getWhatsappReport(Long id);
}
