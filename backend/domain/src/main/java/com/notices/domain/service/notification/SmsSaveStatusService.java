package com.notices.domain.service.notification;

import com.notices.domain.entity.notification.StatusReportSmsEntity;

public interface SmsSaveStatusService {
    StatusReportSmsEntity saveData(String requestParams, String requestBody);

    StatusReportSmsEntity saveData(StatusReportSmsEntity entity);

}
