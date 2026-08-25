package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.domain.*;
import com.bluesky.simulations.domain.enumeration.RankTier;
import com.bluesky.simulations.repository.AircraftRepository;
import com.bluesky.simulations.repository.AirportRepository;
import com.bluesky.simulations.repository.PilotProfileRepository;
import com.bluesky.simulations.repository.UserRepository;
import com.bluesky.simulations.security.SecurityUtils;
import com.bluesky.simulations.service.CareerProgressionService;
import com.bluesky.simulations.service.NavigationCalculationService;
import com.bluesky.simulations.service.dto.FlightLogDTO;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import com.bluesky.simulations.service.dto.RoutePlanDTO;
import com.bluesky.simulations.service.mapper.AircraftMapper;
import com.bluesky.simulations.service.mapper.AirportMapper;
import com.bluesky.simulations.service.mapper.FlightLogMapper;
import com.bluesky.simulations.service.mapper.PilotProfileMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/navigation")
@RequiredArgsConstructor
public class NavigationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NavigationResource.class);
    private final UserRepository userRepository;
    private final PilotProfileRepository pilotProfileRepository;

    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportMapper airportMapper;
    private final AircraftMapper aircraftMapper;
    private final NavigationCalculationService navigationCalculationService;
    private final CareerProgressionService careerProgressionService;
    private final FlightLogMapper flightLogMapper;
    private final PilotProfileMapper pilotProfileMapper;

    @GetMapping("/calculate-route")
    public ResponseEntity<RoutePlanDTO> calcRoute(
        @RequestParam Long departureAirportId,
        @RequestParam Long arrivalAirportId,
        @RequestParam Long aircraftId
    ) {
        if (arrivalAirportId.equals(departureAirportId)) {
            throw new IllegalArgumentException("Departure and Airrival airport cannot be the same.");
        }

        Airport dep = airportRepository
            .findById(departureAirportId)
            .orElseThrow(() -> new IllegalArgumentException("Departure airport was not found."));

        Airport arr = airportRepository
            .findById(arrivalAirportId)
            .orElseThrow(() -> new IllegalArgumentException("Arrival airport was not found."));

        Aircraft ac = aircraftRepository
            .findById(aircraftId)
            .orElseThrow(() -> new IllegalArgumentException("Aircraft could not be found."));

        double distance = navigationCalculationService.calcDistanceNm(
            dep.getLatitude(),
            dep.getLongitude(),
            arr.getLatitude(),
            arr.getLongitude()
        );

        Integer heading = navigationCalculationService.calcInitialHeading(
            dep.getLatitude(),
            dep.getLongitude(),
            arr.getLatitude(),
            arr.getLongitude()
        );

        double flightTime = navigationCalculationService.calcFlightTimeHours(distance, ac.getCruiseSpeedKnots());

        double fuelRequired = navigationCalculationService.calcFuelReqGal(flightTime, ac.getFuelBurnGph());

        int xp = (int) (distance * 1.5) + 50;

        RoutePlanDTO plan = new RoutePlanDTO(
            airportMapper.toDto(dep),
            airportMapper.toDto(arr),
            aircraftMapper.toDto(ac),
            distance,
            heading,
            flightTime,
            fuelRequired,
            xp
        );

        return ResponseEntity.ok(plan);
    }

    @PostMapping("/file-flight")
    public ResponseEntity<FlightLogDTO> fileFlight(@RequestBody RoutePlanDTO plan) {
        LOG.debug("REST request to file and log flight: {}", plan);

        FlightLog savedLog = careerProgressionService.fileFlight(plan);
        FlightLogDTO result = flightLogMapper.toDto(savedLog);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/current-pilot")
    public ResponseEntity<PilotProfileDTO> getCurrentPilot() {
        PilotProfile profile = careerProgressionService.getCurrentPilotProfile();
        return ResponseEntity.ok(pilotProfileMapper.toDto(profile));
    }
}
