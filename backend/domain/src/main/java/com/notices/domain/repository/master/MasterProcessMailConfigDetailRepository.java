package com.notices.domain.repository.master;

import com.notices.domain.entity.master.MasterProcessMailConfigDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessMailConfigDetailRepository extends JpaRepository<MasterProcessMailConfigDetailEntity, Long> {
    Optional<MasterProcessMailConfigDetailEntity> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
