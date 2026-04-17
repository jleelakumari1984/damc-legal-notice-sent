package com.notices.worker.service.notification;

import com.notices.domain.entity.notification.StatusReportSmsEntity;

import java.util.List;

public interface SmsStatusService {

 
    List<StatusReportSmsEntity> getAll();

    StatusReportSmsEntity getById(Long id);
}
