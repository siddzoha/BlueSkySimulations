package com.bluesky.simulations.service;

import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import com.bluesky.simulations.service.dto.RouteProfilePointDTO;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NavigationCalculationService {

    private static final double EARTH_RADIUS_NM = 3440.065;
    private static final double RESERVE_FUEL_HOURS = 0.75;
    private static final double CLIMB_DESCENT_HOURS = 0.2;

    public double calcDistanceNm(double lat1, double long1, double lat2, double long2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLong = Math.toRadians(long2 - long1);
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLong / 2) * Math.sin(dLong / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return Math.round(EARTH_RADIUS_NM * c * 10.0) / 10.0;
    }

    public int calcInitialHeading(double lat1, double long1, double lat2, double long2) {
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dLon = Math.toRadians(long2 - long1);

        double y = Math.sin(dLon) * Math.cos(phi2);
        double x = Math.cos(phi1) * Math.sin(phi2) - Math.sin(phi1) * Math.cos(phi2) * Math.cos(dLon);

        double bearing = Math.toDegrees(Math.atan2(y, x));
        int heading = (int) Math.round((bearing + 360) % 360);

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

        // Distance ceiling: roughly 100-120 ft per nautical mile
        int distCeiling = maxTerrain + (int) (distanceNm * 110);
        int maxPossibleAlt = Math.min(ceiling, distCeiling);

        // Minimum safe cruise altitude
        int minAlt = maxTerrain + 2_000;
        if (minAlt > maxPossibleAlt) {
            maxPossibleAlt = minAlt;
        }

        boolean isEastbound = initialHeading >= 0 && initialHeading < 180;

        // Find the highest valid semicircular altitude <= maxPossibleAlt
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

        // Fallback to minAlt rounded to nearest thousand if candidate dipped too low
        return Math.max(3_000, ((minAlt + 999) / 1_000) * 1_000);
    }

    /**
     * Calculates climb vertical speed in ft/min based on category, weight ratio, and Cost Index.
     */
    public int calcClimbRateFpm(AircraftCategory category, int takeoffWeightLbs, int costIndex) {
        int mtow = getEstimatedMtow(category);
        double weightRatio = Math.max(0.5, Math.min(1.05, (double) takeoffWeightLbs / mtow));

        // Cost Index Factor: CI 0 -> 0.85, CI 50 -> 1.0, CI 100 -> 1.15
        int clampedCi = Math.max(0, Math.min(100, costIndex));
        double ciFactor = 0.85 + 0.30 * (clampedCi / 100.0);

        int baseClimb = switch (category != null ? category : AircraftCategory.NARROWBODY_JET) {
            case PISTON_SINGLE -> 750;
            case TURBOPROP -> 1_600;
            case REGIONAL_JET -> 2_500;
            case NARROWBODY_JET -> 2_800;
            case WIDEBODY_JET -> 2_400;
        };

        // Heavier weight reduces climb rate
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
     * Generates a series of waypoints along the Great Circle path with realistic climb,
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

        // If distance is too short for a full cruise segment, intersect climb and descent
        if (climbDistNm + descentDistNm > totalDistNm && totalDistNm > 0) {
            double ratio = totalDistNm / (climbDistNm + descentDistNm);
            climbDistNm *= ratio;
            descentDistNm *= ratio;
            cruiseAltFt = (int) (depElev + (cruiseAltFt - depElev) * ratio);
        }

        double todDistNm = Math.max(climbDistNm, totalDistNm - descentDistNm);

        int numSegments = 30;
        for (int i = 0; i <= numSegments; i++) {
            double fraction = (double) i / numSegments;
            double currentDist = fraction * totalDistNm;

            // Compute Great Circle intermediate coordinates
            double[] coords = interpolateGreatCircle(lat1, lon1, lat2, lon2, fraction);

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

            waypoints.add(new RouteProfilePointDTO(coords[0], coords[1], alt, Math.round(currentDist * 10.0) / 10.0, vs, phase));
        }

        return waypoints;
    }

    /**
     * Calculates intermediate coordinates along a Great Circle arc at given fraction [0.0 - 1.0].
     */
    private double[] interpolateGreatCircle(double lat1, double lon1, double lat2, double lon2, double f) {
        if (f <= 0.0) return new double[] { lat1, lon1 };
        if (f >= 1.0) return new double[] { lat2, lon2 };

        double phi1 = Math.toRadians(lat1);
        double lambda1 = Math.toRadians(lon1);
        double phi2 = Math.toRadians(lat2);
        double lambda2 = Math.toRadians(lon2);

        double dLat = phi2 - phi1;
        double dLon = lambda2 - lambda1;
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(phi1) * Math.cos(phi2) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double d = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        if (d < 1e-6) {
            return new double[] { lat1, lon1 };
        }

        double A = Math.sin((1 - f) * d) / Math.sin(d);
        double B = Math.sin(f * d) / Math.sin(d);

        double x = A * Math.cos(phi1) * Math.cos(lambda1) + B * Math.cos(phi2) * Math.cos(lambda2);
        double y = A * Math.cos(phi1) * Math.sin(lambda1) + B * Math.cos(phi2) * Math.sin(lambda2);
        double z = A * Math.sin(phi1) + B * Math.sin(phi2);

        double lat = Math.toDegrees(Math.atan2(z, Math.sqrt(x * x + y * y)));
        double lon = Math.toDegrees(Math.atan2(y, x));

        return new double[] { Math.round(lat * 100000.0) / 100000.0, Math.round(lon * 100000.0) / 100000.0 };
    }
}
