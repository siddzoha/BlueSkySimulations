package com.bluesky.simulations.service.dto;

import java.io.Serializable;

public record RoutePlanDTO(
    AirportDTO departureAirport,
    AirportDTO arrivalAirport,
    AircraftDTO aircraft,
    Double distanceNm,
    Integer initHeading,
    Double estFlightTimeHours,
    Double estFuelGallons,
    Integer xpReward
) implements Serializable {}
