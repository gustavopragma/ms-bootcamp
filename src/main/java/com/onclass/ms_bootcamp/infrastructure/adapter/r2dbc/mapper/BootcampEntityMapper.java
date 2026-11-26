package com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.mapper;

import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.entity.BootcampEntity;
import org.springframework.stereotype.Component;

@Component
public class BootcampEntityMapper {
    public BootcampEntity toEntity(Bootcamp bootcamp) {
        return new BootcampEntity(
            bootcamp.getId(),
            bootcamp.getName(),
            bootcamp.getDescription(),
            bootcamp.getLaunchDate(),
            bootcamp.getDuration()
        );
    }

    public Bootcamp toDomain(BootcampEntity bootcampEntity) {
        return new Bootcamp(
            bootcampEntity.getId(),
            bootcampEntity.getName(),
            bootcampEntity.getDescription(),
            bootcampEntity.getLaunchDate(),
            bootcampEntity.getDuration(),
            null,
            null
        );
    }
}
