package com.bluesky.simulations.service.mapper;

import static com.bluesky.simulations.domain.AircraftAsserts.*;
import static com.bluesky.simulations.domain.AircraftTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AircraftMapperTest {

    private AircraftMapper aircraftMapper;

    @BeforeEach
    void setUp() {
        aircraftMapper = new AircraftMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAircraftSample1();
        var actual = aircraftMapper.toEntity(aircraftMapper.toDto(expected));
        assertAircraftAllPropertiesEquals(expected, actual);
    }
}
