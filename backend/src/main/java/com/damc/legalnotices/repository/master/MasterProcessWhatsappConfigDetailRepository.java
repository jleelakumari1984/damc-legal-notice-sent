package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MasterProcessWhatsappConfigDetailRepository
        extends JpaRepository<MasterProcessWhatsappConfigDetailEntity, Long> {
    // @EntityGraph(attributePaths = { "process" })
    // Optional<MasterProcessWhatsappConfigDetailEntity>
    // findFirstByProcessIdAndStatus(Long processId, Integer status);

    @EntityGraph(attributePaths = { "hearingStage" })
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);
}
