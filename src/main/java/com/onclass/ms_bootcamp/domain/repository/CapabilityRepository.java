package com.onclass.ms_bootcamp.domain.repository;

import com.onclass.ms_bootcamp.domain.model.Capability;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapabilityRepository {
    Mono<Void> associateCapabilities(Long bootcampId, List<Long> capabilityIds);
    Flux<Capability> findCapabilitiesByBootcampId(Long bootcampId);
}
