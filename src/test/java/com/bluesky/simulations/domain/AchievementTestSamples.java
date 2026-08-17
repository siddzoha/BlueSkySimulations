package com.bluesky.simulations.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AchievementTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Achievement getAchievementSample1() {
        return new Achievement().id(1L).code("code1").title("title1").description("description1").badgeIcon("badgeIcon1").xpReward(1);
    }

    public static Achievement getAchievementSample2() {
        return new Achievement().id(2L).code("code2").title("title2").description("description2").badgeIcon("badgeIcon2").xpReward(2);
    }

    public static Achievement getAchievementRandomSampleGenerator() {
        return new Achievement()
            .id(longCount.incrementAndGet())
            .code(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .badgeIcon(UUID.randomUUID().toString())
            .xpReward(intCount.incrementAndGet());
    }
}
