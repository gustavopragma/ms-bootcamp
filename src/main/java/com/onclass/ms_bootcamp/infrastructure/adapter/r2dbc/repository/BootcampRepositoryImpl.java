package com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.repository;

import com.onclass.ms_bootcamp.domain.model.Bootcamp;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.entity.BootcampEntity;
import com.onclass.ms_bootcamp.infrastructure.adapter.r2dbc.mapper.BootcampEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BootcampRepositoryImpl implements BootcampRepository {

    private final MySqlBootcampRepository mySqlBootcampRepository;
    private final BootcampEntityMapper bootcampEntityMapper;

    @Override
    public Mono<Boolean> existsByName(String name) {
        return mySqlBootcampRepository.existsByNameIgnoreCase(name);
    }

    @Override
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        BootcampEntity bootcampEntity = bootcampEntityMapper.toEntity(bootcamp);
        return mySqlBootcampRepository.save(bootcampEntity)
            .map(savedBootcamp -> {
                Bootcamp domainBootcamp = bootcampEntityMapper.toDomain(savedBootcamp);
                domainBootcamp.setCapabilityIds(bootcamp.getCapabilityIds());
                return domainBootcamp;
            });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return mySqlBootcampRepository.deleteById(id);
    }

    @Override
    public Flux<Bootcamp> findAll() {
        return mySqlBootcampRepository.findAll()
            .map(bootcampEntityMapper::toDomain);
    }
}
