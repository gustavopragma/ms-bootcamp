package com.onclass.ms_bootcamp.infrastructure.router;

import com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto.BootcampResponseDto;
import com.onclass.ms_bootcamp.infrastructure.adapter.rest.dto.RegisterBootcampRequestDto;
import com.onclass.ms_bootcamp.infrastructure.exceptionHandler.ErrorResponse;
import com.onclass.ms_bootcamp.infrastructure.handler.BootcampHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class BootcampRouter {

    @Bean
    @RouterOperations({
        @RouterOperation(
            path = "/api/v1/bootcamps",
            method = RequestMethod.POST,
            beanClass = BootcampHandler.class,
            beanMethod = "registerBootcamp",
            operation = @Operation(
                operationId = "registerBootcamp",
                summary = "Register a new bootcamp",
                requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = RegisterBootcampRequestDto.class))
                ),
                responses = {
                    @ApiResponse(responseCode = "201", description = "Bootcamp created",
                        content = @Content(schema = @Schema(implementation = BootcampResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data or capability count"),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/bootcamps",
            method = RequestMethod.GET,
            beanClass = BootcampHandler.class,
            beanMethod = "listBootcamps",
            operation = @Operation(
                operationId = "listBootcamps",
                summary = "List bootcamps paginated and sorted",
                parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "name", allowableValues = {"name", "capabilityCount"})),
                    @Parameter(name = "sortDirection", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "asc", allowableValues = {"asc", "desc"}))
                },
                responses = {
                    @ApiResponse(responseCode = "200", description = "List of bootcamps",
                        content = @Content(array = @ArraySchema(schema = @Schema(implementation = BootcampResponseDto.class)))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                }
            )
        ),
        @RouterOperation(
            path = "/api/v1/bootcamps/{bootcampId}",
            method = RequestMethod.DELETE,
            beanClass = BootcampHandler.class,
            beanMethod = "deleteBootcamp",
            operation = @Operation(
                operationId = "deleteBootcamp",
                summary = "delete a bootcamp and its capabilities and technologies",
                parameters = {
                    @Parameter(name = "bootcampId", in = ParameterIn.PATH, required = true, description = "Bootcamp id", example = "1")
                },
                responses = {
                    @ApiResponse(responseCode = "204", description = "Bootcamp deleted successfully"),
                    @ApiResponse(responseCode = "400", description = "Bootcamp not found",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                }
            )
        )
    })
    public RouterFunction<ServerResponse> bootcampRoutes(BootcampHandler handler) {
        return RouterFunctions
            .route(POST("/api/v1/bootcamps"), handler::registerBootcamp)
            .andRoute(GET("/api/v1/bootcamps"), handler::listBootcamps)
            .andRoute(DELETE("/api/v1/bootcamps/{bootcampId}"), handler::deleteBootcamp);
    }
}
