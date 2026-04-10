package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.ProcessExcelMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessExcelMappingRepository extends JpaRepository<ProcessExcelMappingEntity, Long> {
    List<ProcessExcelMappingEntity> findByProcessId(Long processId);
}
