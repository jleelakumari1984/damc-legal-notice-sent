package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MasterProcessWhatsappConfigDetailRepository
        extends JpaRepository<MasterProcessWhatsappConfigDetailEntity, Long> {

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join e.process p left join e.createdUser u WHERE   p.id = :processId")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessId(Long processId);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join e.process p left join e.createdUser u WHERE e.status = :status and p.id = :processId")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join e.process p left join e.createdUser u WHERE e.approveStatus = 0")
    Page<MasterProcessWhatsappConfigDetailEntity> findPendingTemplates(Pageable pageable);
}
