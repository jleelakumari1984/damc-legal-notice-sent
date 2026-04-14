package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendNonLoanSmsDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SendNonLoanSmsDetailRepository extends JpaRepository<SendNonLoanSmsDetailEntity, Long> {
    Optional<SendNonLoanSmsDetailEntity> findByAckId(String ackId);
    List<SendNonLoanSmsDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
