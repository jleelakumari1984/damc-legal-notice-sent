package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.MasterProcessWhatsappConfigDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MasterProcessWhatsappConfigDetailRepository extends JpaRepository<MasterProcessWhatsappConfigDetail, Long> {
    Optional<MasterProcessWhatsappConfigDetail> findFirstByProcessIdAndStatus(Long processId, Integer status);
}
