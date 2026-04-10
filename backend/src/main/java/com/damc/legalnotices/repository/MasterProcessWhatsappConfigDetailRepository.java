package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessWhatsappConfigDetailRepository
        extends JpaRepository<MasterProcessWhatsappConfigDetailEntity, Long> {
    @EntityGraph(attributePaths = { "process" })
    Optional<MasterProcessWhatsappConfigDetailEntity> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
