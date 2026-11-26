package com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CapabilityResponseDto {
    private Long id;
    private String name;
    private List<TechnologyResponseDto> technologies;
}
