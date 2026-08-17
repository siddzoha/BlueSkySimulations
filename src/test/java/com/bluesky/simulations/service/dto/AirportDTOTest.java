package com.bluesky.simulations.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AirportDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AirportDTO.class);
        AirportDTO airportDTO1 = new AirportDTO();
        airportDTO1.setId(1L);
        AirportDTO airportDTO2 = new AirportDTO();
        assertThat(airportDTO1).isNotEqualTo(airportDTO2);
        airportDTO2.setId(airportDTO1.getId());
        assertThat(airportDTO1).isEqualTo(airportDTO2);
        airportDTO2.setId(2L);
        assertThat(airportDTO1).isNotEqualTo(airportDTO2);
        airportDTO1.setId(null);
        assertThat(airportDTO1).isNotEqualTo(airportDTO2);
    }
}
