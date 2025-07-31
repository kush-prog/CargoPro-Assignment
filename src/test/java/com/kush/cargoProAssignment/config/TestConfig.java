package com.kush.cargoProAssignment.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.modelmapper.ModelMapper;

@TestConfiguration
@Profile("test")
public class TestConfig {

    @Bean
    @Primary
    public ModelMapper testModelMapper() {
        return new ModelMapper();
    }
}