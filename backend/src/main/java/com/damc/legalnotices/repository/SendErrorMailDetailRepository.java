package com.damc.legalnotices.repository;

import com.damc.legalnotices.entity.SendErrorMailDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SendErrorMailDetailRepository extends JpaRepository<SendErrorMailDetailEntity, Long> {
}
