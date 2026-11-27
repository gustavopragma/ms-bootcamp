package com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.entity.BootcampEntity;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MySqlBootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
    Mono<Boolean> existsByNameIgnoreCase(String name);

    @Modifying
    @Query("INSERT INTO bootcamps (id, name, description, launch_date, duration) " +
        "VALUES (:id, :name, :description, :launchDate, :duration)")
    Mono<Void> saveWithId(Long id, String name, String description, LocalDate launchDate, Integer duration);
}
