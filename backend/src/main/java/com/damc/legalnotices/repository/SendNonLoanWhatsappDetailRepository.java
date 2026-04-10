package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.SendNonLoanWhatsappDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SendNonLoanWhatsappDetailRepository extends JpaRepository<SendNonLoanWhatsappDetailEntity, Long> {
    Optional<SendNonLoanWhatsappDetailEntity> findByAckId(String ackId);
    List<SendNonLoanWhatsappDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
