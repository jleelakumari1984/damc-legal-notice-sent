package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessDocConfigDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MasterProcessDocConfigDetailRepository extends JpaRepository<MasterProcessDocConfigDetailEntity, Long> {
    List<MasterProcessDocConfigDetailEntity> findByProcessId(Long processId);
}
