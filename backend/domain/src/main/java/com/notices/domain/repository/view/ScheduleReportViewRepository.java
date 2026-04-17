package com.notices.domain.repository.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notices.domain.entity.view.ScheduleReportViewEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleReportViewRepository extends JpaRepository<ScheduleReportViewEntity, Long> {

    List<ScheduleReportViewEntity> findAllByOrderByCreatedAtDesc();

    List<ScheduleReportViewEntity> findByStatus(String status);

    List<ScheduleReportViewEntity> findByProcessSno(Long processSno);

    List<ScheduleReportViewEntity> findByCreatedBy(Long createdBy);

    @Query("SELECT e FROM ScheduleReportViewEntity e " +
            "WHERE (:userId IS NULL OR e.createdBy = :userId) " +
            "AND (:noticeName IS NULL OR LOWER(e.templateName) LIKE LOWER(CONCAT('%', :noticeName, '%'))) " +
            "AND (:status IS NULL OR e.status = :status) " +
            "AND (:fromDate IS NULL OR e.createdAt >= :fromDate) " +
            "AND (:toDate IS NULL OR e.createdAt <= :toDate)")
    Page<ScheduleReportViewEntity> findAllFiltered(
            @Param("userId") Long userId,
            @Param("noticeName") String noticeName,
            @Param("status") String status,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable);
}
