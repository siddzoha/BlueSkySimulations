package com.bluesky.simulations.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FlightLogTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static FlightLog getFlightLogSample1() {
        return new FlightLog().id(1L).distanceNm(1).cruiseAltitudeFt(1).xpEarned(1).remarks("remarks1");
    }

    public static FlightLog getFlightLogSample2() {
        return new FlightLog().id(2L).distanceNm(2).cruiseAltitudeFt(2).xpEarned(2).remarks("remarks2");
    }

    public static FlightLog getFlightLogRandomSampleGenerator() {
        return new FlightLog()
            .id(longCount.incrementAndGet())
            .distanceNm(intCount.incrementAndGet())
            .cruiseAltitudeFt(intCount.incrementAndGet())
            .xpEarned(intCount.incrementAndGet())
            .remarks(UUID.randomUUID().toString());
    }
}
