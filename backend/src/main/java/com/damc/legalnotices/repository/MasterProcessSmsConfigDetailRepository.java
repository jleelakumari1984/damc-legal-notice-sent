package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessSmsConfigDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessSmsConfigDetailRepository extends JpaRepository<MasterProcessSmsConfigDetail, Long> {
    Optional<MasterProcessSmsConfigDetail> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
