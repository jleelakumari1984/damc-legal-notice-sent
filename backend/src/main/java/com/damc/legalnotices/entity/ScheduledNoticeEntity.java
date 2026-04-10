package com.damc.legalnotices.entity;

import com.damc.legalnotices.enums.ProcessingStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "scheduled_notices")
public class ScheduledNoticeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_sno", nullable = false)
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
    private ProcessingStatus status = ProcessingStatus.EXCELUPLOADED;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "failure_reason")
    private String failureReason;
    
    @OneToMany(mappedBy = "scheduledNotice")
    private List<ScheduledNoticeItemEntity> items = new ArrayList<>();
}
