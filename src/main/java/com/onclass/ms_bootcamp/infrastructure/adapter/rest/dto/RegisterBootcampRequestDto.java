package com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterBootcampRequestDto {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "description is required")
    private String description;

    @NotNull(message = "launchDate is required")
    private LocalDate launchDate;

    @NotNull(message = "duration is required")
    @Positive(message = "duration must be greater than 0")
    private Integer duration;

    @NotEmpty(message = "capability ids list cannot be empty")
    private List<Long> capabilityIds;
}
