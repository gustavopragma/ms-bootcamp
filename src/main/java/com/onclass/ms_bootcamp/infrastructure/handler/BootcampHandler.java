package com.onclass.ms_bootcamp.infrastructure.handler;

import com.onclass.ms_bootcamp.application.usecase.DeleteBootcampUseCase;
import com.onclass.ms_bootcamp.application.usecase.ListBootcampUseCase;
import com.onclass.ms_bootcamp.application.usecase.RegisterBootcampUseCase;
import com.onclass.ms_bootcamp.domain.exception.BusinessException;
import com.onclass.ms_bootcamp.domain.exception.ErrorCode;
import com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto.RegisterBootcampRequestDto;
import com.onclass.ms_bootcamp.infrastructure.adapter.rest.mapper.BootcampRestMapper;
import com.onclass.ms_bootcamp.infrastructure.validation.ValidationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BootcampHandler {

    private static final String PAGE_PARAM = "page";
    private static final String SIZE_PARAM = "size";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String SORT_DIRECTION_PARAM = "sortDirection";

    private final ListBootcampUseCase listBootcampUseCase;
    private final RegisterBootcampUseCase registerBootcampUseCase;
    private final DeleteBootcampUseCase deleteBootcampUseCase;
    private final BootcampRestMapper bootcampRestMapper;
    private final ValidationDto validationDto;

    public Mono<ServerResponse> registerBootcamp(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(RegisterBootcampRequestDto.class)
            .flatMap(validationDto::validate)
            .map(bootcampRestMapper::toDomain)
            .flatMap(registerBootcampUseCase::register)
            .map(bootcampRestMapper::toBootcampResponseDto)
            .flatMap(responseDto ->
                ServerResponse.status(HttpStatus.CREATED)
                    .contentType(MediaType.APPLICATION_JSON).bodyValue(responseDto));
    }

    public Mono<ServerResponse> listBootcamps(ServerRequest serverRequest) {
        int page;
        int size;

        try {
            page = Integer.parseInt(serverRequest.queryParam(PAGE_PARAM).orElse("0"));
            size = Integer.parseInt(serverRequest.queryParam(SIZE_PARAM).orElse("10"));
        } catch (NumberFormatException ex) {
            return Mono.error(new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMS));
        }

        String sortBy = serverRequest.queryParam(SORT_BY_PARAM).orElse("name");
        String sortDirection = serverRequest.queryParam(SORT_DIRECTION_PARAM).orElse("ASC");

        return listBootcampUseCase.getBootcamps(page, size, sortBy, sortDirection)
            .map(bootcampRestMapper::toBootcampWithCapabilitiesResponseDto)
            .collectList()
            .flatMap(bootcampResponseDto ->
                ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(bootcampResponseDto));
    }

    public Mono<ServerResponse> deleteBootcamp(ServerRequest serverRequest) {
        Long id = Long.valueOf(serverRequest.pathVariable("bootcampId"));
        return deleteBootcampUseCase.deleteBootcamp(id)
            .then(ServerResponse.noContent().build());
    }
}
