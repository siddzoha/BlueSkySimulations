package com.bluesky.simulations.service.mapper;

import static com.bluesky.simulations.domain.AchievementAsserts.*;
import static com.bluesky.simulations.domain.AchievementTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AchievementMapperTest {

    private AchievementMapper achievementMapper;

    @BeforeEach
    void setUp() {
        achievementMapper = new AchievementMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getAchievementSample1();
        var actual = achievementMapper.toEntity(achievementMapper.toDto(expected));
        assertAchievementAllPropertiesEquals(expected, actual);
    }
}
