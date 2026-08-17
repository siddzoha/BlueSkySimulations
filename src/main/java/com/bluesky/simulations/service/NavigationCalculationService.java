package com.bluesky.simulations.service;

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
}
