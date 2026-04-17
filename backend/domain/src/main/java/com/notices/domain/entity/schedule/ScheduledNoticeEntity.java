package com.notices.domain.entity.schedule;

import com.notices.domain.entity.master.MasterProcessTemplateDetailEntity;
import com.notices.domain.enums.NoticeScheduleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Entity
@Table(name = "scheduled_notices")
public class ScheduledNoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_sno", nullable = false)
    private MasterProcessTemplateDetailEntity process;

    @Column(name = "process_sno", insertable = false, updatable = false)
    private Long processSno;

    @Column(name = "zip_file_path", nullable = false)
    private String zipFilePath;

    @Column(name = "extracted_folder_path", nullable = false)
    private String extractedFolderPath;

    @Column(name = "original_file_name", nullable = false)
    private String originalFileName;

    @Column(name = "send_sms", nullable = false)
    private Boolean sendSms;

    @Column(name = "send_whatsapp", nullable = false)
    private Boolean sendWhatsapp;

    @Column(name = "identifier", nullable = false)
    private Long identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoticeScheduleStatus status = NoticeScheduleStatus.EXCELUPLOADED;

    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @OneToMany(mappedBy = "scheduledNotice")
    private List<ScheduledNoticeItemEntity> items = new ArrayList<>();
}
