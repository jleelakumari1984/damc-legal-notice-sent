package com.notices.domain.repository.master;

import com.notices.domain.entity.master.MasterProcessWhatsappConfigDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MasterProcessWhatsappConfigDetailRepository
        extends JpaRepository<MasterProcessWhatsappConfigDetailEntity, Long> {

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId ")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessId(Long processId);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e  left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId and e.status = :status")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndStatus(Long processId, Boolean status);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE   p.id = :processId and e.createdBy = :createdBy")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndCreatedBy(Long processId, Long createdBy);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.status = :status and p.id = :processId and e.approveStatus = :approveStatus")
    List<MasterProcessWhatsappConfigDetailEntity> findByProcessIdAndStatusAndApprovedStatus(Long processId,
            Integer status, Integer approveStatus);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.approveStatus = 0")
    Page<MasterProcessWhatsappConfigDetailEntity> findPendingTemplates(Pageable pageable);

    @Query("SELECT e FROM MasterProcessWhatsappConfigDetailEntity e left join fetch e.process p left join fetch e.createdUser u WHERE e.approveStatus = 0 and e.createdBy = :userId")
    Page<MasterProcessWhatsappConfigDetailEntity> findPendingTemplatesByCreatedBy(Long userId, Pageable pagination);
}
