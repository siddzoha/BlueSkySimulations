package com.bluesky.simulations.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bluesky.simulations.service.NavigationCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NavigationCalculationServiceTest {

    private NavigationCalculationService navigationCalculationService;

    // CYYZ
    private static final double CYYZ_LAT = 43.6777;
    private static final double CYYZ_LONG = -79.6248;

    //CYUL
    private static final double CYUL_LAT = 45.4786;
    private static final double CYUL_LONG = -73.7408;

    @BeforeEach
    void setUp() {
        navigationCalculationService = new NavigationCalculationService();
    }

    @Test
    void shouldCalculateHeadingFromTorontoToMontreal() {
        int heading = navigationCalculationService.calcInitialHeading(CYYZ_LAT, CYYZ_LONG, CYUL_LAT, CYUL_LONG);
        assertThat(heading).isEqualTo(65);
    }

    @Test
    void shouldCalculateFlightTimeForDash8() {
        double flightTime = navigationCalculationService.calcFlightTimeHours(272.5, 360);

        assertThat(flightTime).isBetween(0.9, 1.1);
    }

    @Test
    void shouldCalculateFuelRequiredWithReserve() {
        // Dash 8 burns 260 GPH, 0.96 hr flight + 0.75 hr reserve = 1.7 hrs * 260 GPH = ~444.6 Gallons
        double fuel = navigationCalculationService.calcFuelReqGal(0.96, 260.0);

        assertThat(fuel).isBetween(430.0, 460.0);
    }
}
