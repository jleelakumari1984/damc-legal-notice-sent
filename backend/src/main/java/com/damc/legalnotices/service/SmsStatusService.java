package com.damc.legalnotices.service;

import com.damc.legalnotices.entity.StatusReportSmsEntity;

import java.util.List;

public interface SmsStatusService {

    StatusReportSmsEntity saveData(String requestParams, String requestBody);

    StatusReportSmsEntity saveData(StatusReportSmsEntity entity);

    List<StatusReportSmsEntity> getAll();

    StatusReportSmsEntity getById(Long id);
}
