package com.onclass.ms_bootcamp.infrastructure.adapter.webclient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssociateCapabilitiesRequestDto {
    private Long bootcampId;
    private List<Long> capabilityIds;
}
