package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.CreditTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditTransactionRepository extends JpaRepository<CreditTransactionEntity, Long> {
    List<CreditTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
}
