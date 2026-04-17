package com.damc.legalnotices.repository.schedule;

import com.damc.legalnotices.entity.schedule.ScheduledNoticeItemEntity;
import com.damc.legalnotices.enums.NoticeScheduleStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduledNoticeItemRepository extends JpaRepository<ScheduledNoticeItemEntity, Long> {

    List<ScheduledNoticeItemEntity> findByScheduledNoticeIdAndStatus(Long scheduledNoticeId,
            NoticeScheduleStatus status);

    long countByScheduledNoticeId(Long scheduledNoticeId);

    long countByScheduledNoticeIdAndStatus(Long scheduledNoticeId, NoticeScheduleStatus status);

    List<ScheduledNoticeItemEntity> findByScheduledNoticeId(Long scheduledNoticeId);

    @Query("SELECT DISTINCT p FROM ScheduledNoticeItemEntity p LEFT JOIN FETCH p.scheduledNotice where p.identifier = :identifier")
    List<ScheduledNoticeItemEntity> findByIdentifier(long identifier);

    @Modifying
    @Query(value = "UPDATE scheduled_notice_items SET identifier = :identifier, status = :status WHERE status = :pendingStatus ORDER BY created_at ASC LIMIT 50", nativeQuery = true)
    int claimPendingItems(@Param("identifier") long identifier, @Param("status") String status,
            @Param("pendingStatus") String pendingStatus);

    @Modifying
    @Query(value = "UPDATE ScheduledNoticeItemEntity SET status = :updateStatus WHERE scheduledNotice.id = :noticeId and status = 'PENDING'", nativeQuery = true)
    void updateStatusByScheduledNoticeId(@Param("noticeId") Long noticeId, @Param("updateStatus") String updateStatus);

    @Modifying
    @Query(value = "UPDATE ScheduledNoticeItemEntity SET status = :updateStatus WHERE scheduledNotice.id = :noticeId AND (status = 'STOP' or status = 'UPLOADED,')", nativeQuery = true)
    void updateStatusByScheduledNoticeIdAndStatus(@Param("noticeId") Long noticeId,
            @Param("updateStatus") String updateStatus, @Param("currentStatus") String currentStatus);
}
