package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterProcessDocConfigDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterProcessDocConfigDetailRepository extends JpaRepository<MasterProcessDocConfigDetailEntity, Long> {
    List<MasterProcessDocConfigDetailEntity> findByProcessId(Long processId);
}
