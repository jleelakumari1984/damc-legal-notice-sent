package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessTemplateDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MasterProcessTemplateDetailRepository extends JpaRepository<MasterProcessTemplateDetailEntity, Long> {

    @Query("SELECT DISTINCT p FROM MasterProcessTemplateDetailEntity p LEFT JOIN FETCH p.excelMappings")
    List<MasterProcessTemplateDetailEntity> findAllWithExcelMappings();

    @Query("SELECT DISTINCT p FROM MasterProcessTemplateDetailEntity p LEFT JOIN FETCH p.excelMappings WHERE p.id = :id")
    Optional<MasterProcessTemplateDetailEntity> findByIdWithExcelMappings(Long id);
}
