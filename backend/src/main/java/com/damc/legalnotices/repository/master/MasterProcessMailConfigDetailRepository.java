package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessMailConfigDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessMailConfigDetailRepository extends JpaRepository<MasterProcessMailConfigDetailEntity, Long> {
    Optional<MasterProcessMailConfigDetailEntity> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
