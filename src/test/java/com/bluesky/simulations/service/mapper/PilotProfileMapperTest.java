package com.bluesky.simulations.service.mapper;

import static com.bluesky.simulations.domain.PilotProfileAsserts.*;
import static com.bluesky.simulations.domain.PilotProfileTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PilotProfileMapperTest {

    private PilotProfileMapper pilotProfileMapper;

    @BeforeEach
    void setUp() {
        pilotProfileMapper = new PilotProfileMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPilotProfileSample1();
        var actual = pilotProfileMapper.toEntity(pilotProfileMapper.toDto(expected));
        assertPilotProfileAllPropertiesEquals(expected, actual);
    }
}
