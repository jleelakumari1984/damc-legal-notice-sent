package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterProcessWhatsappConfigDetailRepository
        extends JpaRepository<MasterProcessWhatsappConfigDetailEntity, Long> {

    List<MasterProcessWhatsappConfigDetailEntity> findByProcessId(Long processId);
    @EntityGraph(attributePaths = { "hearingStage" })
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);
}
