package com.damc.legalnotices.repository.view;

import org.springframework.data.jpa.repository.JpaRepository;

import com.damc.legalnotices.entity.view.ScheduleReportViewEntity;

import java.util.List;

public interface ScheduleReportViewRepository extends JpaRepository<ScheduleReportViewEntity, Long> {

    List<ScheduleReportViewEntity> findAllByOrderByCreatedAtDesc();

    List<ScheduleReportViewEntity> findByStatus(String status);

    List<ScheduleReportViewEntity> findByProcessSno(Long processSno);

    List<ScheduleReportViewEntity> findByCreatedBy(Long createdBy);
}
