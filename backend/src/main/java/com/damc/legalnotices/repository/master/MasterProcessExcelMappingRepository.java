package com.damc.legalnotices.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;

import com.damc.legalnotices.entity.master.MasterProcessExcelMappingEntity;

import java.util.List;

public interface MasterProcessExcelMappingRepository extends JpaRepository<MasterProcessExcelMappingEntity, Long> {
    List<MasterProcessExcelMappingEntity> findByProcessId(Long processId);
}
