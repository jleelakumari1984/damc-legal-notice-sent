package com.damc.legalnotices.repository.user;

import com.damc.legalnotices.entity.user.UserActivityLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLogRepository extends JpaRepository<UserActivityLogEntity, Long> {

    Page<UserActivityLogEntity> findByUserId(Long userId, Pageable pageable);

    Page<UserActivityLogEntity> findByPerformedBy(Long performedBy, Pageable pageable);
}
