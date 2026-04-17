package com.notices.domain.repository.schedule;

import com.notices.domain.entity.schedule.ScheduledNoticeEntity;
import com.notices.domain.enums.NoticeScheduleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduledNoticeRepository extends JpaRepository<ScheduledNoticeEntity, Long> {

    @Query("SELECT n FROM ScheduledNoticeEntity n JOIN FETCH n.process")
    List<ScheduledNoticeEntity> findAllWithProcess();

    @Query("SELECT n FROM ScheduledNoticeEntity n JOIN FETCH n.process WHERE n.id = :id")
    Optional<ScheduledNoticeEntity> findByIdWithProcess(@Param("id") Long id);
    
    List<ScheduledNoticeEntity> findTop50ByStatusOrderByCreatedAtAsc(NoticeScheduleStatus status);

    @Modifying
    @Query(value = "UPDATE scheduled_notices SET identifier = :identifier, status = :NoticeScheduleStatus WHERE status = :pendingStatus ORDER BY created_at ASC LIMIT 10", nativeQuery = true)
    int claimPendingForExcelParsing(@Param("identifier") long identifier,
            @Param("NoticeScheduleStatus") String NoticeScheduleStatus,
            @Param("pendingStatus") String pendingStatus);

    List<ScheduledNoticeEntity> findByStatus(NoticeScheduleStatus status);

    List<ScheduledNoticeEntity> findByStatusIn(List<NoticeScheduleStatus> statuses);

    List<ScheduledNoticeEntity> findByIdentifier(long identifier);
}
