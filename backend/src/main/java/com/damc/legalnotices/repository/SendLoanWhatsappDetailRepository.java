package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.SendLoanWhatsappDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SendLoanWhatsappDetailRepository extends JpaRepository<SendLoanWhatsappDetailEntity, Long> {
    Optional<SendLoanWhatsappDetailEntity> findByAckId(String ackId);
    List<SendLoanWhatsappDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
