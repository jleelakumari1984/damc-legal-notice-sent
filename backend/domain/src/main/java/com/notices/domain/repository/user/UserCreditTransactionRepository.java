package com.notices.domain.repository.user;

import com.notices.domain.entity.user.UserCreditTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserCreditTransactionRepository extends JpaRepository<UserCreditTransactionEntity, Long> {
    List<UserCreditTransactionEntity> findByUserIdOrderByCreatedAtDesc(Long userId);
    Page<UserCreditTransactionEntity> findByUserId(Long userId, Pageable pageable);
}
