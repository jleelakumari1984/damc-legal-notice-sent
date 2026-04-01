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
@Table(name = "master_process_whatsapp_config_details")
public class MasterProcessWhatsappConfigDetail {

    @Id
    @Column(name = "sno")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "process_sno")
    private MasterProcessTemplateDetail process;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "template_path")
    private String templatePath;

    @Column(name = "status")
    private Integer status;
}
