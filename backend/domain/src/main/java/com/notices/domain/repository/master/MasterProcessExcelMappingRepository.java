package com.notices.domain.repository.master;

import org.springframework.data.jpa.repository.JpaRepository;

import com.notices.domain.entity.master.MasterProcessExcelMappingEntity;

import java.util.List;

public interface MasterProcessExcelMappingRepository extends JpaRepository<MasterProcessExcelMappingEntity, Long> {
    List<MasterProcessExcelMappingEntity> findByProcessId(Long processId);
}
