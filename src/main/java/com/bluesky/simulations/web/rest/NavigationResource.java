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
import com.bluesky.simulations.service.dto.RouteProfilePointDTO;
import com.bluesky.simulations.service.mapper.AircraftMapper;
import com.bluesky.simulations.service.mapper.AirportMapper;
import com.bluesky.simulations.service.mapper.FlightLogMapper;
import com.bluesky.simulations.service.mapper.PilotProfileMapper;
import java.io.Serializable;
import java.util.List;
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

    public record AircraftWeightLimitsDTO(
        int emptyWeightLbs,
        int maxTakeoffWeightLbs,
        int recommendedTakeoffWeightLbs
    ) implements Serializable {}

    @GetMapping("/aircraft-weight-limits/{aircraftId}")
    public ResponseEntity<AircraftWeightLimitsDTO> getAircraftWeightLimits(@PathVariable Long aircraftId) {
        Aircraft ac = aircraftRepository
            .findById(aircraftId)
            .orElseThrow(() -> new IllegalArgumentException("Aircraft could not be found with ID: " + aircraftId));

        int mtow = navigationCalculationService.getEstimatedMtow(ac.getCategory());
        int empty = navigationCalculationService.getEstimatedEmptyWeight(ac.getCategory());
        int recommended = (int) Math.round(mtow * 0.82);

        return ResponseEntity.ok(new AircraftWeightLimitsDTO(empty, mtow, recommended));
    }

    @GetMapping("/calculate-route")
    public ResponseEntity<RoutePlanDTO> calcRoute(
        @RequestParam Long departureAirportId,
        @RequestParam Long arrivalAirportId,
        @RequestParam Long aircraftId,
        @RequestParam(required = false) Integer takeoffWeightLbs,
        @RequestParam(required = false) Integer costIndex
    ) {
        if (arrivalAirportId.equals(departureAirportId)) {
            throw new IllegalArgumentException("Departure and Arrival airport cannot be the same.");
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

        int mtow = navigationCalculationService.getEstimatedMtow(ac.getCategory());
        int effectiveWeight = takeoffWeightLbs != null && takeoffWeightLbs > 0 ? takeoffWeightLbs : (int) Math.round(mtow * 0.82);

        int effectiveCostIndex = costIndex != null && costIndex >= 0 ? costIndex : 30;

        int depElev = dep.getElevationFt() != null ? dep.getElevationFt() : 0;
        int arrElev = arr.getElevationFt() != null ? arr.getElevationFt() : 0;

        int targetCruiseAlt = navigationCalculationService.calcTargetCruiseAltitude(
            heading,
            distance,
            ac.getServiceCeilingFt(),
            depElev,
            arrElev
        );

        int climbRateFpm = navigationCalculationService.calcClimbRateFpm(ac.getCategory(), effectiveWeight, effectiveCostIndex);

        int descentRateFpm = navigationCalculationService.calcDescentRateFpm(ac.getCategory(), effectiveCostIndex);

        List<RouteProfilePointDTO> profileWaypoints = navigationCalculationService.generateProfileWaypoints(
            dep.getLatitude(),
            dep.getLongitude(),
            depElev,
            arr.getLatitude(),
            arr.getLongitude(),
            arrElev,
            distance,
            targetCruiseAlt,
            climbRateFpm,
            descentRateFpm,
            ac.getCruiseSpeedKnots()
        );

        RoutePlanDTO plan = new RoutePlanDTO(
            airportMapper.toDto(dep),
            airportMapper.toDto(arr),
            aircraftMapper.toDto(ac),
            distance,
            heading,
            flightTime,
            fuelRequired,
            xp,
            effectiveWeight,
            effectiveCostIndex,
            targetCruiseAlt,
            climbRateFpm,
            descentRateFpm,
            profileWaypoints
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
