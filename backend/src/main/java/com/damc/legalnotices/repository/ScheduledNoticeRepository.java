package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.ScheduledNotice;
import com.damc.legalnotices.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledNoticeRepository extends JpaRepository<ScheduledNotice, Long> {
    List<ScheduledNotice> findTop50ByStatusOrderByCreatedAtAsc(ProcessingStatus status);
}
