package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendLoanWhatsappDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SendLoanWhatsappDetailRepository extends JpaRepository<SendLoanWhatsappDetailEntity, Long> {
    Optional<SendLoanWhatsappDetailEntity> findByAckId(String ackId);

    List<SendLoanWhatsappDetailEntity> findAllByAckIdIsNullAndSendResponseIsNotNull();

    @Query("SELECT s FROM SendLoanWhatsappDetailEntity s left join fetch s.process  left join fetch s.whatsappConfig WHERE s.scheduleSno = :noticeId AND s.sendResponse IS NOT NULL AND s.receivedStatus = :receivedStatus")
    Page<SendLoanWhatsappDetailEntity> findByScheduleSnoAndReceivedStatus(Long noticeId, String receivedStatus,
            Pageable pageable);

    @Query("SELECT s FROM SendLoanWhatsappDetailEntity s left join fetch s.process  left join fetch s.whatsappConfig WHERE s.scheduleSno = :noticeId")
    Page<SendLoanWhatsappDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
