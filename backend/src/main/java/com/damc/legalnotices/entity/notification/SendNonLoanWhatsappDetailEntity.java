package com.damc.legalnotices.entity.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;

@Getter
@Setter
@Entity
@Table(name = "send_non_loan_whatsapp_details")
public class SendNonLoanWhatsappDetailEntity {
    @ManyToOne
    @JoinColumn(name = "process_sno", insertable = false, updatable = false)
    private MasterProcessTemplateDetailEntity process;

    @ManyToOne
    @JoinColumn(name = "whatsapp_template_sno", insertable = false, updatable = false)
    private MasterProcessWhatsappConfigDetailEntity whatsappConfig;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sno")
    private Long id;

    @Column(name = "type", length = 45)
    private String type;

    @Column(name = "schedule_sno")
    private Long scheduleSno;

    @Column(name = "process_sno")
    private Long processSno;

    @Column(name = "whatsapp_template_sno")
    private Long whatsappTemplateSno;

    @Column(name = "send_to", length = 15)
    private String sendTo;

    @Column(name = "message", columnDefinition = "longtext")
    private String message;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "send_status")
    private Integer sendStatus;

    @Column(name = "send_response", columnDefinition = "longtext")
    private String sendResponse;

    @Column(name = "ack_id")
    private String ackId;

    @Column(name = "received_status", length = 50)
    private String receivedStatus;

    @Column(name = "received_at")
    private Instant receivedAt;

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
}
