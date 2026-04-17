package com.notices.domain.repository.notification;

import com.notices.domain.entity.notification.SendErrorWhatsappDetailEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SendErrorWhatsappDetailRepository extends JpaRepository<SendErrorWhatsappDetailEntity, Long> {

    @Query("SELECT s FROM SendErrorWhatsappDetailEntity s left join fetch s.process  left join fetch s.whatsappConfig WHERE s.scheduleSno = :noticeId")
    Page<SendErrorWhatsappDetailEntity> findByScheduleSno(Long noticeId, Pageable pageable);
}
