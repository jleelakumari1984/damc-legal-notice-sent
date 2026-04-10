package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.ProcessingStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduledNoticeItemRepository extends JpaRepository<ScheduledNoticeItemEntity, Long> {

    List<ScheduledNoticeItemEntity> findByScheduledNoticeIdAndStatus(Long scheduledNoticeId, ProcessingStatus status);

    long countByScheduledNoticeIdAndStatus(Long scheduledNoticeId, ProcessingStatus status);

    List<ScheduledNoticeItemEntity> findByScheduledNoticeId(Long scheduledNoticeId);

    @Query("SELECT DISTINCT p FROM ScheduledNoticeItemEntity p LEFT JOIN FETCH p.scheduledNotice")
    List<ScheduledNoticeItemEntity> findByIdentifier(long identifier);

    @Modifying
    @Query(value = "UPDATE scheduled_notice_items SET identifier = :identifier, status = :processingStatus WHERE status = :pendingStatus ORDER BY created_at ASC LIMIT 50", nativeQuery = true)
    int claimPendingItems(long identifier, String processingStatus, String pendingStatus);
}
