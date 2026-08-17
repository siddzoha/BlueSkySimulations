package com.bluesky.simulations.domain;

import static com.bluesky.simulations.domain.AirportTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AirportTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Airport.class);
        Airport airport1 = getAirportSample1();
        Airport airport2 = new Airport();
        assertThat(airport1).isNotEqualTo(airport2);

        airport2.setId(airport1.getId());
        assertThat(airport1).isEqualTo(airport2);

        airport2 = getAirportSample2();
        assertThat(airport1).isNotEqualTo(airport2);
    }
}
