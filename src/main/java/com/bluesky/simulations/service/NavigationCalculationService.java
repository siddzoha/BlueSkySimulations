package com.bluesky.simulations.service;

import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import com.bluesky.simulations.service.dto.RouteProfilePointDTO;
import java.util.ArrayList;
import java.util.List;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import net.sf.geographiclib.GeodesicLine;
import org.springframework.stereotype.Service;

/**
 * Service for aeronautical navigation calculations and vertical flight profile synthesis.
 * Uses GeographicLib for ellipsoidal WGS-84 geodesic calculations.
 */
@Service
public class NavigationCalculationService {

    private static final double METERS_PER_NAUTICAL_MILE = 1852.0;
    private static final double RESERVE_FUEL_HOURS = 0.75;
    private static final double CLIMB_DESCENT_HOURS = 0.2;

    /**
     * Calculates ellipsoidal distance in Nautical Miles between two coordinates using WGS-84 Geodesic.
     */
    public double calcDistanceNm(double lat1, double long1, double lat2, double long2) {
        GeodesicData g = Geodesic.WGS84.Inverse(lat1, long1, lat2, long2);
        double distNm = g.s12 / METERS_PER_NAUTICAL_MILE;
        return Math.round(distNm * 10.0) / 10.0;
    }

    /**
     * Calculates the true initial heading (azimuth 1°–360°) using WGS-84 Geodesic inverse problem.
     */
    public int calcInitialHeading(double lat1, double long1, double lat2, double long2) {
        GeodesicData g = Geodesic.WGS84.Inverse(lat1, long1, lat2, long2);
        int heading = (int) Math.round((g.azi1 + 360.0) % 360.0);
        return heading == 0 ? 360 : heading;
    }

    public double calcFlightTimeHours(double distNm, int cruiseSpeedKnots) {
        double totalTime = distNm / cruiseSpeedKnots + CLIMB_DESCENT_HOURS;
        return Math.round(totalTime * 100.0) / 100.0;
    }

    public double calcFuelReqGal(double flightTimeHours, double fuelBurnGph) {
        double totalFuel = (flightTimeHours + RESERVE_FUEL_HOURS) * fuelBurnGph;
        return Math.round(totalFuel * 10.0) / 10.0;
    }

    /**
     * Estimates Maximum Takeoff Weight (MTOW) in lbs based on aircraft category.
     */
    public int getEstimatedMtow(AircraftCategory category) {
        if (category == null) return 150_000;
        return switch (category) {
            case PISTON_SINGLE -> 2_550;
            case TURBOPROP -> 45_000;
            case REGIONAL_JET -> 85_000;
            case NARROWBODY_JET -> 174_000;
            case WIDEBODY_JET -> 650_000;
        };
    }

    /**
     * Estimates Empty Weight in lbs based on aircraft category.
     */
    public int getEstimatedEmptyWeight(AircraftCategory category) {
        if (category == null) return 80_000;
        return switch (category) {
            case PISTON_SINGLE -> 1_650;
            case TURBOPROP -> 26_000;
            case REGIONAL_JET -> 45_000;
            case NARROWBODY_JET -> 90_000;
            case WIDEBODY_JET -> 320_000;
        };
    }

    /**
     * Calculates the target cruise altitude respecting:
     * 1. Aircraft service ceiling
     * 2. Trip distance (short hops cannot reach high flight levels)
     * 3. Semicircular rule (Eastbound 0-179° = ODD thousands, Westbound 180-359° = EVEN thousands)
     */
    public int calcTargetCruiseAltitude(int initialHeading, double distanceNm, Integer serviceCeilingFt, int depElev, int arrElev) {
        int ceiling = serviceCeilingFt != null && serviceCeilingFt > 0 ? serviceCeilingFt : 35_000;
        int maxTerrain = Math.max(depElev, arrElev);

        // Distance ceiling: roughly 110 ft per nautical mile
        int distCeiling = maxTerrain + (int) (distanceNm * 110);
        int maxPossibleAlt = Math.min(ceiling, distCeiling);

        int minAlt = maxTerrain + 2_000;
        if (minAlt > maxPossibleAlt) {
            maxPossibleAlt = minAlt;
        }

        boolean isEastbound = initialHeading >= 0 && initialHeading < 180;

        int candidate = (maxPossibleAlt / 1_000) * 1_000;
        while (candidate >= minAlt) {
            int thousands = candidate / 1_000;
            boolean isOdd = thousands % 2 != 0;

            if (isEastbound && isOdd) {
                return candidate;
            } else if (!isEastbound && !isOdd) {
                return candidate;
            }
            candidate -= 1_000;
        }

        return Math.max(3_000, ((minAlt + 999) / 1_000) * 1_000);
    }

    /**
     * Calculates climb vertical speed in ft/min based on category, weight ratio, and Cost Index.
     */
    public int calcClimbRateFpm(AircraftCategory category, int takeoffWeightLbs, int costIndex) {
        int mtow = getEstimatedMtow(category);
        double weightRatio = Math.max(0.5, Math.min(1.05, (double) takeoffWeightLbs / mtow));

        int clampedCi = Math.max(0, Math.min(100, costIndex));
        double ciFactor = 0.85 + 0.30 * (clampedCi / 100.0);

        int baseClimb = switch (category != null ? category : AircraftCategory.NARROWBODY_JET) {
            case PISTON_SINGLE -> 750;
            case TURBOPROP -> 1_600;
            case REGIONAL_JET -> 2_500;
            case NARROWBODY_JET -> 2_800;
            case WIDEBODY_JET -> 2_400;
        };

        double weightFactor = 1.35 - 0.45 * weightRatio;
        return (int) Math.round(baseClimb * weightFactor * ciFactor);
    }

    /**
     * Calculates descent vertical speed in ft/min based on category and Cost Index.
     */
    public int calcDescentRateFpm(AircraftCategory category, int costIndex) {
        int clampedCi = Math.max(0, Math.min(100, costIndex));
        double ciFactor = 0.85 + 0.30 * (clampedCi / 100.0);

        int baseDescent = switch (category != null ? category : AircraftCategory.NARROWBODY_JET) {
            case PISTON_SINGLE -> 650;
            case TURBOPROP -> 1_400;
            case REGIONAL_JET -> 1_800;
            case NARROWBODY_JET -> 2_000;
            case WIDEBODY_JET -> 1_900;
        };

        return -((int) Math.round(baseDescent * ciFactor));
    }

    /**
     * Generates a series of waypoints along the Great Circle (WGS-84 Geodesic) with realistic climb,
     * cruise, and descent altitude profile data.
     */
    public List<RouteProfilePointDTO> generateProfileWaypoints(
        double lat1,
        double lon1,
        int depElev,
        double lat2,
        double lon2,
        int arrElev,
        double totalDistNm,
        int cruiseAltFt,
        int climbRateFpm,
        int descentRateFpm,
        int cruiseSpeedKnots
    ) {
        List<RouteProfilePointDTO> waypoints = new ArrayList<>();

        double climbSpeedKnots = cruiseSpeedKnots * 0.75;
        double descentSpeedKnots = cruiseSpeedKnots * 0.80;

        double climbDistNm = ((double) (cruiseAltFt - depElev) / Math.max(500, climbRateFpm)) * (climbSpeedKnots / 60.0);
        double descentDistNm = ((double) (cruiseAltFt - arrElev) / Math.max(500, Math.abs(descentRateFpm))) * (descentSpeedKnots / 60.0);

        if (climbDistNm + descentDistNm > totalDistNm && totalDistNm > 0) {
            double ratio = totalDistNm / (climbDistNm + descentDistNm);
            climbDistNm *= ratio;
            descentDistNm *= ratio;
            cruiseAltFt = (int) (depElev + (cruiseAltFt - depElev) * ratio);
        }

        double todDistNm = Math.max(climbDistNm, totalDistNm - descentDistNm);

        // Precompute WGS-84 Geodesic line for precise waypoints
        GeodesicLine line = Geodesic.WGS84.InverseLine(lat1, lon1, lat2, lon2);
        double totalDistanceMeters = totalDistNm * METERS_PER_NAUTICAL_MILE;

        int numSegments = 30;
        for (int i = 0; i <= numSegments; i++) {
            double fraction = (double) i / numSegments;
            double currentDist = fraction * totalDistNm;

            // Compute precise position along WGS-84 Geodesic line
            GeodesicData pt = line.Position(fraction * totalDistanceMeters);
            double lat = Math.round(pt.lat2 * 100000.0) / 100000.0;
            double lon = Math.round(pt.lon2 * 100000.0) / 100000.0;

            int alt;
            int vs;
            String phase;

            if (currentDist <= climbDistNm && climbDistNm > 0) {
                double climbFraction = currentDist / climbDistNm;
                alt = (int) Math.round(depElev + (cruiseAltFt - depElev) * climbFraction);
                vs = climbRateFpm;
                phase = "CLIMB";
            } else if (currentDist < todDistNm) {
                alt = cruiseAltFt;
                vs = 0;
                phase = "CRUISE";
            } else {
                double descentFraction = descentDistNm > 0 ? (currentDist - todDistNm) / descentDistNm : 1.0;
                descentFraction = Math.min(1.0, Math.max(0.0, descentFraction));
                alt = (int) Math.round(cruiseAltFt - (cruiseAltFt - arrElev) * descentFraction);
                vs = descentRateFpm;
                phase = "DESCENT";
            }

            waypoints.add(new RouteProfilePointDTO(lat, lon, alt, Math.round(currentDist * 10.0) / 10.0, vs, phase));
        }

        return waypoints;
    }
}
