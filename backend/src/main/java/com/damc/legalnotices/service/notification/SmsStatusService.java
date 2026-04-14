package com.damc.legalnotices.service.notification;

import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;

import java.util.List;

public interface SmsStatusService {

    StatusReportSmsEntity saveData(String requestParams, String requestBody);

    StatusReportSmsEntity saveData(StatusReportSmsEntity entity);

    List<StatusReportSmsEntity> getAll();

    StatusReportSmsEntity getById(Long id);
}
