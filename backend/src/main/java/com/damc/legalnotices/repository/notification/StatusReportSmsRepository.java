package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.StatusReportSmsEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReportSmsRepository extends JpaRepository<StatusReportSmsEntity, Long> {

    // Custom query to find records where ack_id is null
    List<StatusReportSmsEntity> findByProcessDateIsNull();
}
