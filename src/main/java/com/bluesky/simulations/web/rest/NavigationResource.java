package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.domain.Aircraft;
import com.bluesky.simulations.domain.Airport;
import com.bluesky.simulations.repository.AircraftRepository;
import com.bluesky.simulations.repository.AirportRepository;
import com.bluesky.simulations.service.NavigationCalculationService;
import com.bluesky.simulations.service.dto.RoutePlanDTO;
import com.bluesky.simulations.service.mapper.AircraftMapper;
import com.bluesky.simulations.service.mapper.AirportMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/navigation")
@RequiredArgsConstructor
public class NavigationResource {

    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;
    private final AirportMapper airportMapper;
    private final AircraftMapper aircraftMapper;
    private final NavigationCalculationService navigationCalculationService;

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
}
