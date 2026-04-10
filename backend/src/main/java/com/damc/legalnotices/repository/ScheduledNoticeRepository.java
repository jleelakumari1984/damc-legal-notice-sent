package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.ScheduledNoticeEntity;
import com.damc.legalnotices.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduledNoticeRepository extends JpaRepository<ScheduledNoticeEntity, Long> {

    List<ScheduledNoticeEntity> findTop50ByStatusOrderByCreatedAtAsc(ProcessingStatus status);

    @Modifying
    @Query(value = "UPDATE scheduled_notices SET identifier = :identifier, status = :processingStatus WHERE status = :pendingStatus ORDER BY created_at ASC LIMIT 10", nativeQuery = true)
    int claimPendingForExcelParsing(@Param("identifier") long identifier,
            @Param("processingStatus") String processingStatus,
            @Param("pendingStatus") String pendingStatus);

    List<ScheduledNoticeEntity> findByStatus(ProcessingStatus status);

    List<ScheduledNoticeEntity> findByStatusIn(List<ProcessingStatus> statuses);

    List<ScheduledNoticeEntity> findByIdentifier(long identifier);
}
