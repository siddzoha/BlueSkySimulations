package com.bluesky.simulations.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import com.bluesky.simulations.service.dto.RouteProfilePointDTO;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class NavigationCalculationServiceTest {

    private NavigationCalculationService navigationCalculationService;

    // CYYZ (Toronto Pearson)
    private static final double CYYZ_LAT = 43.6777;
    private static final double CYYZ_LONG = -79.6248;
    private static final int CYYZ_ELEV = 569;

    // CYUL (Montreal Trudeau)
    private static final double CYUL_LAT = 45.4786;
    private static final double CYUL_LONG = -73.7408;
    private static final int CYUL_ELEV = 118;

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

    @Test
    void shouldCalculateTargetCruiseAltitudeObeyingSemicircularRule() {
        // Eastbound (heading 65°): Must be an ODD flight level (thousands % 2 != 0)
        int eastAlt = navigationCalculationService.calcTargetCruiseAltitude(65, 272.5, 41_000, CYYZ_ELEV, CYUL_ELEV);
        int eastThousands = eastAlt / 1_000;
        assertThat(eastThousands % 2).isNotEqualTo(0);
        assertThat(eastAlt).isLessThanOrEqualTo(41_000);

        // Westbound (heading 245°): Must be an EVEN flight level (thousands % 2 == 0)
        int westAlt = navigationCalculationService.calcTargetCruiseAltitude(245, 272.5, 41_000, CYUL_ELEV, CYYZ_ELEV);
        int westThousands = westAlt / 1_000;
        assertThat(westThousands % 2).isEqualTo(0);
        assertThat(westAlt).isLessThanOrEqualTo(41_000);
    }

    @Test
    void shouldCalculateClimbRateInfluencedByWeightAndCostIndex() {
        AircraftCategory cat = AircraftCategory.NARROWBODY_JET;
        int mtow = navigationCalculationService.getEstimatedMtow(cat);

        // Heavier weight should result in lower climb rate
        int lightClimb = navigationCalculationService.calcClimbRateFpm(cat, (int) (mtow * 0.60), 30);
        int heavyClimb = navigationCalculationService.calcClimbRateFpm(cat, (int) (mtow * 0.98), 30);
        assertThat(lightClimb).isGreaterThan(heavyClimb);

        // Higher Cost Index should increase climb rate
        int econClimb = navigationCalculationService.calcClimbRateFpm(cat, (int) (mtow * 0.80), 0);
        int fastClimb = navigationCalculationService.calcClimbRateFpm(cat, (int) (mtow * 0.80), 90);
        assertThat(fastClimb).isGreaterThan(econClimb);
    }

    @Test
    void shouldCalculateDescentRateInfluencedByCostIndex() {
        AircraftCategory cat = AircraftCategory.NARROWBODY_JET;
        int econDescent = navigationCalculationService.calcDescentRateFpm(cat, 0);
        int fastDescent = navigationCalculationService.calcDescentRateFpm(cat, 90);

        // Both are negative vertical speeds; fastDescent should have larger magnitude
        assertThat(econDescent).isNegative();
        assertThat(fastDescent).isNegative();
        assertThat(Math.abs(fastDescent)).isGreaterThan(Math.abs(econDescent));
    }

    @Test
    void shouldGenerateProfileWaypointsWithClimbCruiseDescentPhases() {
        double dist = navigationCalculationService.calcDistanceNm(CYYZ_LAT, CYYZ_LONG, CYUL_LAT, CYUL_LONG);
        int cruiseAlt = 27_000;
        int climbRate = 2_500;
        int descentRate = -1_800;
        int speed = 400;

        List<RouteProfilePointDTO> waypoints = navigationCalculationService.generateProfileWaypoints(
            CYYZ_LAT,
            CYYZ_LONG,
            CYYZ_ELEV,
            CYUL_LAT,
            CYUL_LONG,
            CYUL_ELEV,
            dist,
            cruiseAlt,
            climbRate,
            descentRate,
            speed
        );

        assertThat(waypoints).isNotNull();
        assertThat(waypoints.size()).isEqualTo(31);

        RouteProfilePointDTO start = waypoints.get(0);
        assertThat(start.altitudeFt()).isEqualTo(CYYZ_ELEV);
        assertThat(start.flightPhase()).isEqualTo("CLIMB");

        RouteProfilePointDTO mid = waypoints.get(15);
        assertThat(mid.altitudeFt()).isEqualTo(cruiseAlt);
        assertThat(mid.flightPhase()).isEqualTo("CRUISE");

        RouteProfilePointDTO end = waypoints.get(waypoints.size() - 1);
        assertThat(end.altitudeFt()).isEqualTo(CYUL_ELEV);
        assertThat(end.flightPhase()).isEqualTo("DESCENT");
    }
}
