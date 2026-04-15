package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterProcessSmsConfigDetailRepository extends JpaRepository<MasterProcessSmsConfigDetailEntity, Long> {

    List<MasterProcessSmsConfigDetailEntity> findByProcessId(Long processId);

    @EntityGraph(attributePaths = {"hearingStage"})
    List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);
}
