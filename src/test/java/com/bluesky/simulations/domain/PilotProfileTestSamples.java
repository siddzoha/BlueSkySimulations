package com.bluesky.simulations.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class PilotProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static PilotProfile getPilotProfileSample1() {
        return new PilotProfile().id(1L).totalXp(1).flightsCompleted(1);
    }

    public static PilotProfile getPilotProfileSample2() {
        return new PilotProfile().id(2L).totalXp(2).flightsCompleted(2);
    }

    public static PilotProfile getPilotProfileRandomSampleGenerator() {
        return new PilotProfile()
            .id(longCount.incrementAndGet())
            .totalXp(intCount.incrementAndGet())
            .flightsCompleted(intCount.incrementAndGet());
    }
}
