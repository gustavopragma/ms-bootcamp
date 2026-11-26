package com.onclass.ms_bootcamp.infrastructure.configuration;

import com.onclass.ms_bootcamp.application.usecase.ListBootcampUseCase;
import com.onclass.ms_bootcamp.application.usecase.RegisterBootcampUseCase;
import com.onclass.ms_bootcamp.domain.repository.BootcampRepository;
import com.onclass.ms_bootcamp.domain.repository.CapabilityRepository;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ListBootcampUseCase listBootcampUseCase(
        BootcampRepository bootcampRepository,
        CapabilityRepository capabilityRepository
    ) {
        return new ListBootcampUseCase(
            bootcampRepository,
            capabilityRepository
        );
    }

    @Bean
    public RegisterBootcampUseCase registerBootcampUseCase(
        BootcampRepository bootcampRepository,
        CapabilityRepository capabilityRepository
    ) {
        return new RegisterBootcampUseCase(
            bootcampRepository,
            capabilityRepository
        );
    }

    @Bean
    public WebProperties.Resources resources() {
        return new WebProperties.Resources();
    }
}
