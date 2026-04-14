package com.damc.legalnotices.repository.excel;

import com.damc.legalnotices.entity.excel.ProcessExcelMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessExcelMappingRepository extends JpaRepository<ProcessExcelMappingEntity, Long> {
    List<ProcessExcelMappingEntity> findByProcessId(Long processId);
}
