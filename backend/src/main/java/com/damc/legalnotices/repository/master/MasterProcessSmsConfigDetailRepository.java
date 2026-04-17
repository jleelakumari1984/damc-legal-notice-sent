package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessSmsConfigDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MasterProcessSmsConfigDetailRepository
                extends JpaRepository<MasterProcessSmsConfigDetailEntity, Long> {

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e  left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId")
        List<MasterProcessSmsConfigDetailEntity> findByProcessId(Long processId);

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e  left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId and e.status = :status")
        List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndStatus(Long processId, Boolean status);

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId and e.createdBy = :createdBy")
        List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndCreatedBy(Long processId, Long createdBy);

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.status = :status and p.id = :processId and e.approveStatus = :approveStatus")
        List<MasterProcessSmsConfigDetailEntity> findByProcessIdAndStatusAndApprovedStatus(Long processId,
                        Integer status,
                        Integer approveStatus);

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.approveStatus = 0")
        Page<MasterProcessSmsConfigDetailEntity> findPendingTemplates(Pageable pageable);

        @Query("SELECT e FROM MasterProcessSmsConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.approveStatus = 0 and e.createdBy = :userId")
        Page<MasterProcessSmsConfigDetailEntity> findPendingTemplatesByCreatedBy(Long userId, Pageable pagination);
}
