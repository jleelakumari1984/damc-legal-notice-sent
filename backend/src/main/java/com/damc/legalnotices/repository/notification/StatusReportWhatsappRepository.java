package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.StatusReportWhatsappEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReportWhatsappRepository extends JpaRepository<StatusReportWhatsappEntity, Long> {

    List<StatusReportWhatsappEntity> findByProcessDateIsNull();
}
