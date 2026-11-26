package com.onclass.ms_bootcamp.infrastructure.adapter.rest.mapper;

import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.model.Capability;
import com.onclass.ms_bootcamp.domain.model.Technology;
import com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto.*;
import org.springframework.stereotype.Component;

@Component
public class BootcampRestMapper {

    public Bootcamp toDomain(RegisterBootcampRequestDto registerBootcampRequestDto) {
        return new Bootcamp(
            null,
            registerBootcampRequestDto.getName(),
            registerBootcampRequestDto.getDescription(),
            registerBootcampRequestDto.getLaunchDate(),
            registerBootcampRequestDto.getDuration(),
            registerBootcampRequestDto.getCapabilityIds(),
            null
        );
    }

    public BootcampResponseDto toBootcampResponseDto(Bootcamp bootcamp) {
        return new BootcampResponseDto(
            bootcamp.getId(),
            bootcamp.getName(),
            bootcamp.getDescription(),
            bootcamp.getLaunchDate(),
            bootcamp.getDuration(),
            bootcamp.getCapabilityIds()
        );
    }

    public BootcampWithCapabilitiesDto toBootcampWithCapabilitiesResponseDto(Bootcamp bootcamp) {
        return new BootcampWithCapabilitiesDto(
            bootcamp.getId(),
            bootcamp.getName(),
            bootcamp.getDescription(),
            bootcamp.getLaunchDate(),
            bootcamp.getDuration(),
            bootcamp.getCapabilities().stream()
                .map(this::toCapabilityResponseDto).toList()
        );
    }

    public CapabilityResponseDto toCapabilityResponseDto(Capability capability) {
        return new CapabilityResponseDto(
            capability.getId(),
            capability.getName(),
            capability.getTechnologies().stream()
                .map(this::toTechnologyResponseDto).toList()
        );
    }

    public TechnologyResponseDto toTechnologyResponseDto(Technology technology) {
        return new TechnologyResponseDto(
            technology.getId(),
            technology.getName()
        );
    }
}
