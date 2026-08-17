package com.bluesky.simulations.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AircraftTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Aircraft getAircraftSample1() {
        return new Aircraft()
            .id(1L)
            .tailNumber("tailNumber1")
            .modelName("modelName1")
            .icaoType("icaoType1")
            .cruiseSpeedKnots(1)
            .maxRangeNm(1)
            .serviceCeilingFt(1)
            .minRunwayLengthFt(1);
    }

    public static Aircraft getAircraftSample2() {
        return new Aircraft()
            .id(2L)
            .tailNumber("tailNumber2")
            .modelName("modelName2")
            .icaoType("icaoType2")
            .cruiseSpeedKnots(2)
            .maxRangeNm(2)
            .serviceCeilingFt(2)
            .minRunwayLengthFt(2);
    }

    public static Aircraft getAircraftRandomSampleGenerator() {
        return new Aircraft()
            .id(longCount.incrementAndGet())
            .tailNumber(UUID.randomUUID().toString())
            .modelName(UUID.randomUUID().toString())
            .icaoType(UUID.randomUUID().toString())
            .cruiseSpeedKnots(intCount.incrementAndGet())
            .maxRangeNm(intCount.incrementAndGet())
            .serviceCeilingFt(intCount.incrementAndGet())
            .minRunwayLengthFt(intCount.incrementAndGet());
    }
}
