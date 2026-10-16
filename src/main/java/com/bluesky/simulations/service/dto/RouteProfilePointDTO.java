package com.bluesky.simulations.service.dto;

import java.io.Serializable;

/**
 * A DTO representing a waypoint along the vertical flight profile,
 * including planned altitude, flight phase, and vertical speed.
 */
public record RouteProfilePointDTO(
    double latitude,
    double longitude,
    int altitudeFt,
    double distanceFromOriginNm,
    int verticalSpeedFpm,
    String flightPhase // "CLIMB", "CRUISE", "DESCENT"
) implements Serializable {}
