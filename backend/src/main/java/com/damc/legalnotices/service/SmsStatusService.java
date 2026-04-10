package com.damc.legalnotices.service;

import com.damc.legalnotices.entity.StatusReportSmsEntity;

import java.util.List;

public interface SmsStatusService {

    void saveData(String requestParams, String requestBody);

    List<StatusReportSmsEntity> getAll();

    StatusReportSmsEntity getById(Long id);
}
