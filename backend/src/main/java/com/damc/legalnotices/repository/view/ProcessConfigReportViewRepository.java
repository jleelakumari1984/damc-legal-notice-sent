package com.damc.legalnotices.repository.view;

import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProcessConfigReportViewRepository extends JpaRepository<ProcessConfigReportViewEntity, Long> {

    List<ProcessConfigReportViewEntity> findAllByOrderByNameAsc();

    @Query("SELECT e FROM ProcessConfigReportViewEntity e      WHERE e.createdBy = :createdBy")
    Page<ProcessConfigReportViewEntity> findAllByCreatedBy(Long createdBy, Pageable pageable);

    @Query("SELECT e FROM ProcessConfigReportViewEntity e   ")
    Page<ProcessConfigReportViewEntity> findAll(Pageable pageable);
}
