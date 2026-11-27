package com.onclass.ms_bootcamp.infrastructure.adapter.webclient.repository;

import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.domain.model.Capability;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import com.onclass.ms_bootcamp.infrastructure.adapter.webclient.dto.AssociateCapabilitiesRequestDto;
import com.onclass.ms_bootcamp.infrastructure.adapter.webclient.dto.CapabilityClientDto;
import com.onclass.ms_bootcamp.infrastructure.adapter.webclient.dto.ErrorResponseDto;
import com.onclass.ms_bootcamp.infrastructure.adapter.webclient.mapper.CapabilityClientDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CapabilityRepositoryImpl implements CapabilityRepository {

    private static final String ASSOCIATE_CAPABILITY_PATH = "/api/v1/capabilities/associate-bootcamp";
    private static final String GET_CAPABILITIES_PATH = "/api/v1/capabilities/by-bootcamp/{bootcampId}";
    private static final String DELETE_CAPABILITIES = "/api/v1/capabilities/by-bootcamp/{bootcampId}";

    private final WebClient webClient;
    private final CapabilityClientDtoMapper capabilityClientDtoMapper;

    @Override
    public Mono<Void> associateCapabilities(Long bootcampId, List<Long> capabilityIds) {
        AssociateCapabilitiesRequestDto request = new AssociateCapabilitiesRequestDto(bootcampId, capabilityIds);

        return webClient.post()
            .uri(ASSOCIATE_CAPABILITY_PATH)
            .bodyValue(request)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.ASSOCIATE_CAPABILITY_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)))
            .bodyToMono(Void.class)
            .onErrorMap(WebClientRequestException.class, ex ->
                new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE));
    }

    @Override
    public Flux<Capability> findCapabilitiesByBootcampId(Long bootcampId) {
        return webClient.get()
            .uri(GET_CAPABILITIES_PATH, bootcampId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.ASSOCIATE_CAPABILITY_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)))
            .bodyToFlux(CapabilityClientDto.class)
            .map(capabilityClientDtoMapper::toDomain)
            .onErrorMap(WebClientRequestException.class, ex ->
                new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE));
    }

    @Override
    public Mono<Void> deleteCapabilitiesByBootcampId(Long bootcampId) {
        return webClient.delete()
            .uri(DELETE_CAPABILITIES, bootcampId)
            .retrieve()
            .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                clientResponse.bodyToMono(ErrorResponseDto.class)
                    .flatMap(errorBody -> Mono.error(
                        new BusinessException(ErrorCode.DELETE_CAPABILITIES_ERROR, errorBody.getDescription())
                    )))
            .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                Mono.error(new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE)))
            .bodyToMono(Void.class)
            .onErrorMap(WebClientRequestException.class, ex ->
                new BusinessException(ErrorCode.CAPABILITY_SERVICE_UNAVAILABLE));
    }
}
