package com.damc.legalnotices.entity.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.damc.legalnotices.entity.user.UserEntity;

@Getter
@Setter
@Entity
@Table(name = "master_process_sms_config_details")
public class MasterProcessSmsConfigDetailEntity {
    @ManyToOne
    @JoinColumn(name = "created_by", updatable = false, insertable = false)
    private UserEntity createdUser;

    @Id
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "process_sno")
    private MasterProcessTemplateDetailEntity process;

    @Column(name = "sent_level")
    private Integer sentLevel;

    @Column(name = "peid")
    private String peid;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "route_id")
    private String routeId;

    @Column(name = "template_path", updatable = false)
    private String templatePath;

    @Column(name = "user_template_path", updatable = false)
    private String userTemplatePath;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "channel")
    private String channel;

    @Column(name = "dcs")
    private Integer dcs;

    @Column(name = "flash_sms")
    private Integer flashSms;

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

    @Column(name = "status")
    private Integer status;

    @Column(name = "approve_status")
    private Integer approveStatus;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
