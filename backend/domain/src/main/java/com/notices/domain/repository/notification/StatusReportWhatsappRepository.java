package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.StatusReportWhatsappEntity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReportWhatsappRepository extends JpaRepository<StatusReportWhatsappEntity, Long> {

    List<StatusReportWhatsappEntity> findByProcessDateIsNull();
}
