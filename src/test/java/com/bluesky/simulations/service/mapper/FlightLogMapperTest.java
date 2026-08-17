package com.bluesky.simulations.service.mapper;

import static com.bluesky.simulations.domain.FlightLogAsserts.*;
import static com.bluesky.simulations.domain.FlightLogTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FlightLogMapperTest {

    private FlightLogMapper flightLogMapper;

    @BeforeEach
    void setUp() {
        flightLogMapper = new FlightLogMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getFlightLogSample1();
        var actual = flightLogMapper.toEntity(flightLogMapper.toDto(expected));
        assertFlightLogAllPropertiesEquals(expected, actual);
    }
}
