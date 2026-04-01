package com.damc.legalnotices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "master_process_sms_config_details")
public class MasterProcessSmsConfigDetail {

    @Id
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "process_sno")
    private MasterProcessTemplateDetail process;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "template_path")
    private String templatePath;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "status")
    private Integer status;
}
