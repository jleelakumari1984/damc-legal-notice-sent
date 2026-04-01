package com.damc.legalnotices.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "master_process_template_details")
public class MasterProcessTemplateDetail {

    @Id
    @Column(name = "sno")
    private Long id;

    @Column(name = "name")
    private String name;
}
