package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendNonLoanWhatsappDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SendNonLoanWhatsappDetailRepository extends JpaRepository<SendNonLoanWhatsappDetailEntity, Long> {
    @Query("SELECT s FROM SendNonLoanWhatsappDetailEntity s left join fetch s.process  left join fetch s.whatsappConfig WHERE s.ackId = :ackId")
    Optional<SendNonLoanWhatsappDetailEntity> findByAckId(String ackId);

    @Query("SELECT s FROM SendNonLoanWhatsappDetailEntity s left join fetch s.process  left join fetch s.whatsappConfig WHERE s.ackId IS NULL AND s.sendResponse IS NOT NULL")
    List<SendNonLoanWhatsappDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();
}
