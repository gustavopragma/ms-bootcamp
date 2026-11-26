package com.onclass.ms_bootcamp.domain.repository;

import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampRepository {
    Mono<Boolean> existsByName(String name);
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Void> deleteById(Long id);
    Flux<Bootcamp> findAll();
}