package com.bluesky.simulations.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class AirportTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + 2 * Short.MAX_VALUE);

    public static Airport getAirportSample1() {
        return new Airport()
            .id(1L)
            .icaoCode("icaoCode1")
            .iataCode("iataCode1")
            .name("name1")
            .city("city1")
            .country("country1")
            .elevationFt(1)
            .longestRunwayFt(1);
    }

    public static Airport getAirportSample2() {
        return new Airport()
            .id(2L)
            .icaoCode("icaoCode2")
            .iataCode("iataCode2")
            .name("name2")
            .city("city2")
            .country("country2")
            .elevationFt(2)
            .longestRunwayFt(2);
    }

    public static Airport getAirportRandomSampleGenerator() {
        return new Airport()
            .id(longCount.incrementAndGet())
            .icaoCode(UUID.randomUUID().toString())
            .iataCode(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString())
            .elevationFt(intCount.incrementAndGet())
            .longestRunwayFt(intCount.incrementAndGet());
    }
}
