package com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("bootcamps")
public class BootcampEntity {
    @Id
    private Long id;

    private String name;

    private String description;

    @Column("launch_date")
    private LocalDate launchDate;

    private Integer duration;
}
