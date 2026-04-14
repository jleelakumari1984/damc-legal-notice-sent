package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendLoanSmsDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SendLoanSmsDetailRepository extends JpaRepository<SendLoanSmsDetailEntity, Long> {
    Optional<SendLoanSmsDetailEntity> findByAckId(String ackId);
    List<SendLoanSmsDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
