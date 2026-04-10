package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessSmsConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessSmsConfigDetailRepository extends JpaRepository<MasterProcessSmsConfigDetailEntity, Long> {
    @EntityGraph(attributePaths = {"process"})
    Optional<MasterProcessSmsConfigDetailEntity> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
