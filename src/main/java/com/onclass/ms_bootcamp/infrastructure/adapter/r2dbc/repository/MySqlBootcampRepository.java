package com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface MySqlBootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
    Mono<Boolean> existsByNameIgnoreCase(String name);
}
