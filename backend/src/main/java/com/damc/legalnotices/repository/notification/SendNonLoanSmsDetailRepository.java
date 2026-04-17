package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendNonLoanSmsDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SendNonLoanSmsDetailRepository extends JpaRepository<SendNonLoanSmsDetailEntity, Long> {

    @Query("SELECT s FROM SendNonLoanSmsDetailEntity s left join fetch s.process  left join fetch s.smsConfig WHERE s.ackId = :ackId")
    Optional<SendNonLoanSmsDetailEntity> findByAckId(String ackId);

    @Query("SELECT s FROM SendNonLoanSmsDetailEntity s left join fetch s.process  left join fetch s.smsConfig WHERE s.ackId IS NULL AND s.sendResponse IS NOT NULL")
    List<SendNonLoanSmsDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
