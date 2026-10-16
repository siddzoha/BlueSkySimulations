package com.bluesky.simulations.service.dto;

import java.io.Serializable;
import java.util.List;

public record RoutePlanDTO(
    AirportDTO departureAirport,
    AirportDTO arrivalAirport,
    AircraftDTO aircraft,
    Double distanceNm,
    Integer initHeading,
    Double estFlightTimeHours,
    Double estFuelGallons,
    Integer xpReward,
    Integer takeoffWeightLbs,
    Integer costIndex,
    Integer targetCruiseAltitudeFt,
    Integer climbRateFpm,
    Integer descentRateFpm,
    List<RouteProfilePointDTO> profileWaypoints
) implements Serializable {}
