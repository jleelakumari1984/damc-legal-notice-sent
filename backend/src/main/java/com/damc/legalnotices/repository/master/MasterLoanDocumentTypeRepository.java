package com.damc.legalnotices.repository.master;

import com.damc.legalnotices.entity.master.MasterLoanDocumentTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MasterLoanDocumentTypeRepository extends JpaRepository<MasterLoanDocumentTypeEntity, Long> {
}
