package com.bluesky.simulations.domain;

import static com.bluesky.simulations.domain.AchievementTestSamples.*;
import static com.bluesky.simulations.domain.PilotProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.bluesky.simulations.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class AchievementTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Achievement.class);
        Achievement achievement1 = getAchievementSample1();
        Achievement achievement2 = new Achievement();
        assertThat(achievement1).isNotEqualTo(achievement2);

        achievement2.setId(achievement1.getId());
        assertThat(achievement1).isEqualTo(achievement2);

        achievement2 = getAchievementSample2();
        assertThat(achievement1).isNotEqualTo(achievement2);
    }

    @Test
    void pilotsTest() {
        Achievement achievement = getAchievementRandomSampleGenerator();
        PilotProfile pilotProfileBack = getPilotProfileRandomSampleGenerator();

        achievement.addPilots(pilotProfileBack);
        assertThat(achievement.getPilotses()).containsOnly(pilotProfileBack);
        assertThat(pilotProfileBack.getAchievements()).containsOnly(achievement);

        achievement.removePilots(pilotProfileBack);
        assertThat(achievement.getPilotses()).doesNotContain(pilotProfileBack);
        assertThat(pilotProfileBack.getAchievements()).doesNotContain(achievement);

        achievement.pilotses(new HashSet<>(Set.of(pilotProfileBack)));
        assertThat(achievement.getPilotses()).containsOnly(pilotProfileBack);
        assertThat(pilotProfileBack.getAchievements()).containsOnly(achievement);

        achievement.setPilotses(new HashSet<>());
        assertThat(achievement.getPilotses()).doesNotContain(pilotProfileBack);
        assertThat(pilotProfileBack.getAchievements()).doesNotContain(achievement);
    }
}
