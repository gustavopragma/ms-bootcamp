package com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto;

import com.onclass.ms_bootcamp.domain.model.Capability;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BootcampWithCapabilitiesDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate launchDate;
    private Integer duration;
    private List<CapabilityResponseDto> capabilities;
}
