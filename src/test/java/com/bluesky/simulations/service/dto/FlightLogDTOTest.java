package com.bluesky.simulations.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FlightLogDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FlightLogDTO.class);
        FlightLogDTO flightLogDTO1 = new FlightLogDTO();
        flightLogDTO1.setId(1L);
        FlightLogDTO flightLogDTO2 = new FlightLogDTO();
        assertThat(flightLogDTO1).isNotEqualTo(flightLogDTO2);
        flightLogDTO2.setId(flightLogDTO1.getId());
        assertThat(flightLogDTO1).isEqualTo(flightLogDTO2);
        flightLogDTO2.setId(2L);
        assertThat(flightLogDTO1).isNotEqualTo(flightLogDTO2);
        flightLogDTO1.setId(null);
        assertThat(flightLogDTO1).isNotEqualTo(flightLogDTO2);
    }
}
