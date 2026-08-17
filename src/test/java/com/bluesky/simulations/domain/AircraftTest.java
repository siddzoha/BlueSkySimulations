package com.bluesky.simulations.domain;

import static com.bluesky.simulations.domain.AircraftTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AircraftTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Aircraft.class);
        Aircraft aircraft1 = getAircraftSample1();
        Aircraft aircraft2 = new Aircraft();
        assertThat(aircraft1).isNotEqualTo(aircraft2);

        aircraft2.setId(aircraft1.getId());
        assertThat(aircraft1).isEqualTo(aircraft2);

        aircraft2 = getAircraftSample2();
        assertThat(aircraft1).isNotEqualTo(aircraft2);
    }
}
