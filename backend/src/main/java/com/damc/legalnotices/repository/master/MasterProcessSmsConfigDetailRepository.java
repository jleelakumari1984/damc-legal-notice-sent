package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterProcessSmsConfigDetailRepository extends JpaRepository<MasterProcessSmsConfigDetailEntity, Long> {
   // @EntityGraph(attributePaths = {"process"})
   // Optional<MasterProcessSmsConfigDetailEntity> findFirstByProcessIdAndStatus(Long processId, Integer status);

    @EntityGraph(attributePaths = {"hearingStage"})
    List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);
}
