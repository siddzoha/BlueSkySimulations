package com.bluesky.simulations.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PilotProfileDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PilotProfileDTO.class);
        PilotProfileDTO pilotProfileDTO1 = new PilotProfileDTO();
        pilotProfileDTO1.setId(1L);
        PilotProfileDTO pilotProfileDTO2 = new PilotProfileDTO();
        assertThat(pilotProfileDTO1).isNotEqualTo(pilotProfileDTO2);
        pilotProfileDTO2.setId(pilotProfileDTO1.getId());
        assertThat(pilotProfileDTO1).isEqualTo(pilotProfileDTO2);
        pilotProfileDTO2.setId(2L);
        assertThat(pilotProfileDTO1).isNotEqualTo(pilotProfileDTO2);
        pilotProfileDTO1.setId(null);
        assertThat(pilotProfileDTO1).isNotEqualTo(pilotProfileDTO2);
    }
}
