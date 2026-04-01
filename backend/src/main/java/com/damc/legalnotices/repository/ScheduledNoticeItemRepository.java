package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.ScheduledNoticeItem;
import com.damc.legalnotices.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduledNoticeItemRepository extends JpaRepository<ScheduledNoticeItem, Long> {
    List<ScheduledNoticeItem> findByScheduledNoticeIdAndStatus(Long scheduledNoticeId, ProcessingStatus status);

    long countByScheduledNoticeIdAndStatus(Long scheduledNoticeId, ProcessingStatus status);
}
