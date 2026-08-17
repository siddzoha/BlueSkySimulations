package com.bluesky.simulations.service.mapper;

import static com.bluesky.simulations.domain.AirportAsserts.*;
import static com.bluesky.simulations.domain.AirportTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AirportMapperTest {

    private AirportMapper airportMapper;

    @BeforeEach
    void setUp() {
        airportMapper = new AirportMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAirportSample1();
        var actual = airportMapper.toEntity(airportMapper.toDto(expected));
        assertAirportAllPropertiesEquals(expected, actual);
    }
}
