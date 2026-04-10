package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.StatusReportSmsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusReportSmsRepository extends JpaRepository<StatusReportSmsEntity, Long> {
}
