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

import com.damc.legalnotices.entity.master.MasterProcessTemplateDetailEntity;
import com.damc.legalnotices.entity.master.MasterProcessWhatsappConfigDetailEntity;

@Getter
@Setter
@Entity
@Table(name = "send_error_whatsapp_details")
public class SendErrorWhatsappDetailEntity {
    
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

    @Column(name = "loan_no")
    private Long loanNo;

    @Column(name = "send_to", length = 15)
    private String sendTo;

    @Column(name = "message", columnDefinition = "longtext")
    private String message;

    @Column(name = "send_at")
    private Instant sendAt;

    @Column(name = "error_message", columnDefinition = "longtext")
    private String errorMessage;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_date", insertable = false, updatable = false)
    private Instant createdDate;
}
