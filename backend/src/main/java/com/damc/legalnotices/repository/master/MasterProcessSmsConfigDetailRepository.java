package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MasterProcessSmsConfigDetailRepository
        extends JpaRepository<MasterProcessSmsConfigDetailEntity, Long> {

    @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join e.process p left join e.createdUser u WHERE   p.id = :processId")
    List<MasterProcessSmsConfigDetailEntity> findByProcessId(Long processId);

    @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join e.process p left join e.createdUser u WHERE e.status = :status and p.id = :processId")
    List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndStatus(Long processId, Integer status);

    @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join e.process p left join e.createdUser u WHERE e.approveStatus = 0")
    Page<MasterProcessSmsConfigDetailEntity> findPendingTemplates(Pageable pageable);
}
