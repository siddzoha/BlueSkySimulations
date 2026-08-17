package com.bluesky.simulations.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AircraftDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(AircraftDTO.class);
        AircraftDTO aircraftDTO1 = new AircraftDTO();
        aircraftDTO1.setId(1L);
        AircraftDTO aircraftDTO2 = new AircraftDTO();
        assertThat(aircraftDTO1).isNotEqualTo(aircraftDTO2);
        aircraftDTO2.setId(aircraftDTO1.getId());
        assertThat(aircraftDTO1).isEqualTo(aircraftDTO2);
        aircraftDTO2.setId(2L);
        assertThat(aircraftDTO1).isNotEqualTo(aircraftDTO2);
        aircraftDTO1.setId(null);
        assertThat(aircraftDTO1).isNotEqualTo(aircraftDTO2);
    }
}
