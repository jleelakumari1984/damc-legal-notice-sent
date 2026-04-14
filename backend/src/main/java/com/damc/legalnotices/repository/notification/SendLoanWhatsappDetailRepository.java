package com.damc.legalnotices.repository.notification;

import com.damc.legalnotices.entity.notification.SendLoanWhatsappDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SendLoanWhatsappDetailRepository extends JpaRepository<SendLoanWhatsappDetailEntity, Long> {
    Optional<SendLoanWhatsappDetailEntity> findByAckId(String ackId);

    List<SendLoanWhatsappDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();

    Page<SendLoanWhatsappDetailEntity> findByScheduleSnoAndReceivedStatus(Long noticeId, String receivedStatus,
            Pageable pageable);

    Page<SendLoanWhatsappDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
