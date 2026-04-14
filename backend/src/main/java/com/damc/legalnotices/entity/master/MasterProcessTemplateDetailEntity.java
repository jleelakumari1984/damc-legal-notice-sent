package com.damc.legalnotices.entity.master;

import com.damc.legalnotices.entity.excel.ProcessExcelMappingEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "master_process_template_details")
public class MasterProcessTemplateDetailEntity {

    @OneToMany(mappedBy = "process")
    private List<ProcessExcelMappingEntity> excelMappings;

    @Id
    @Column(name = "sno")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "step_name")
    private String stepName;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
