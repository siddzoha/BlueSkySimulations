package com.bluesky.simulations.domain;

import static com.bluesky.simulations.domain.AchievementTestSamples.*;
import static com.bluesky.simulations.domain.PilotProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class PilotProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PilotProfile.class);
        PilotProfile pilotProfile1 = getPilotProfileSample1();
        PilotProfile pilotProfile2 = new PilotProfile();
        assertThat(pilotProfile1).isNotEqualTo(pilotProfile2);

        pilotProfile2.setId(pilotProfile1.getId());
        assertThat(pilotProfile1).isEqualTo(pilotProfile2);

        pilotProfile2 = getPilotProfileSample2();
        assertThat(pilotProfile1).isNotEqualTo(pilotProfile2);
    }

    @Test
    void achievementsTest() {
        PilotProfile pilotProfile = getPilotProfileRandomSampleGenerator();
        Achievement achievementBack = getAchievementRandomSampleGenerator();

        pilotProfile.addAchievements(achievementBack);
        assertThat(pilotProfile.getAchievementses()).containsOnly(achievementBack);

        pilotProfile.removeAchievements(achievementBack);
        assertThat(pilotProfile.getAchievementses()).doesNotContain(achievementBack);

        pilotProfile.achievementses(new HashSet<>(Set.of(achievementBack)));
        assertThat(pilotProfile.getAchievementses()).containsOnly(achievementBack);

        pilotProfile.setAchievementses(new HashSet<>());
        assertThat(pilotProfile.getAchievementses()).doesNotContain(achievementBack);
    }
}
