package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.StatusReportSmsEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReportSmsRepository extends JpaRepository<StatusReportSmsEntity, Long> {

    // Custom query to find records where ack_id is null
    List<StatusReportSmsEntity> findByProcessDateIsNull();

}
