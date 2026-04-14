package com.damc.legalnotices.repository.view;

import com.damc.legalnotices.entity.view.ProcessConfigReportViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessConfigReportViewRepository extends JpaRepository<ProcessConfigReportViewEntity, Long> {

    List<ProcessConfigReportViewEntity> findAllByOrderByNameAsc();
}
