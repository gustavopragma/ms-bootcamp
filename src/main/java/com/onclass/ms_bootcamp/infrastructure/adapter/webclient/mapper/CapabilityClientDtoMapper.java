package com.onclass.ms_bootcamp.infrastructure.adapter.webclient.mapper;

import com.onclass.ms_bootcamp.domain.model.Capability;
import com.onclass.ms_bootcamp.domain.model.Technology;
import com.onclass.ms_bootcamp.infrastructure.adapter.webclient.dto.CapabilityClientDto;
import org.springframework.stereotype.Component;

@Component
public class CapabilityClientDtoMapper {
    public Capability toDomain(CapabilityClientDto capabilityClientDto) {
        return new Capability(
            capabilityClientDto.getId(),
            capabilityClientDto.getName(),
            capabilityClientDto.getTechnologies().stream().map(technologyClientDto ->
                    new Technology(technologyClientDto.getId(), technologyClientDto.getName()))
                .toList()
        );
    }
}
