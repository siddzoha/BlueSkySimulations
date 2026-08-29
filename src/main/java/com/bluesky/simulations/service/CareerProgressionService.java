package com.bluesky.simulations.service;

import com.bluesky.simulations.domain.FlightLog;
import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.domain.User;
import com.bluesky.simulations.domain.enumeration.FlightRules;
import com.bluesky.simulations.domain.enumeration.RankTier;
import com.bluesky.simulations.repository.*;
import com.bluesky.simulations.security.SecurityUtils;
import com.bluesky.simulations.service.dto.RoutePlanDTO;
import com.bluesky.simulations.service.impl.PilotProfileServiceImpl;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CareerProgressionService {

    private final FlightLogRepository flightLogRepository;
    private final PilotProfileRepository pilotProfileRepository;
    private final AchievementRepository achievementRepository;
    private final UserRepository userRepository;
    private final AirportRepository airportRepository;
    private final AircraftRepository aircraftRepository;
    private final PilotProfileServiceImpl pilotProfileServiceImpl;

    public RankTier calculateRank(int totalXp) {
        if (totalXp >= 15000) return RankTier.FLEET_CHIEF;
        if (totalXp >= 8000) return RankTier.CAPTAIN;
        if (totalXp >= 3000) return RankTier.SENIOR_FIRST_OFFICER;
        if (totalXp >= 1000) return RankTier.FIRST_OFFICER;
        return RankTier.STUDENT;
    }

    public FlightLog fileFlight(RoutePlanDTO plan) {
        PilotProfile pilotProfile = getCurrentPilotProfile();

        FlightLog log = new FlightLog();
        log.setDepartureTime(Instant.now());
        log.setArrivalTime(Instant.now().plusSeconds((long) (plan.estFlightTimeHours() * 3600)));
        log.setDepartureAirport(airportRepository.findById(plan.departureAirport().getId()).orElseThrow());
        log.setArrivalAirport(airportRepository.findById(plan.arrivalAirport().getId()).orElseThrow());
        log.setAircraft(aircraftRepository.findById(plan.aircraft().getId()).orElseThrow());
        log.setPilot(pilotProfile);
        log.setDurationHours(plan.estFlightTimeHours());
        log.setDistanceNm(plan.distanceNm().intValue());
        log.setFuelUsedGallons(plan.estFuelGallons());
        log.setXpEarned(plan.xpReward());
        log.setFlightRules(FlightRules.IFR);

        // Link database entities
        double currTotalFlightHours = pilotProfile.getTotalFlightHours() != null ? pilotProfile.getTotalFlightHours() : 0.0;
        double updatedTotalHours = Math.round((currTotalFlightHours + plan.estFlightTimeHours()) * 100.0) / 100.0;
        pilotProfile.setTotalFlightHours(updatedTotalHours);

        if (log.getFlightRules() == FlightRules.IFR) {
            double currIfrFlightHours = pilotProfile.getTotalIfrFlightHours() != null ? pilotProfile.getTotalIfrFlightHours() : 0.0;
            double updatedIfrHours = Math.round((currIfrFlightHours + plan.estFlightTimeHours()) * 100.0) / 100.0;
            pilotProfile.setTotalIfrFlightHours(updatedIfrHours);
        }

        pilotProfile.setTotalXp(pilotProfile.getTotalXp() + plan.xpReward());
        pilotProfile.setFlightsCompleted(pilotProfile.getFlightsCompleted() + 1);
        pilotProfile.setRankTier(calculateRank(pilotProfile.getTotalXp()));
        log.setPilot(pilotProfile);

        FlightLog savedLog = flightLogRepository.save(log);

        if (pilotProfile.getFlightsCompleted() >= 1) {
            achievementRepository
                .findAll()
                .stream()
                .filter(a -> "FIRST_FLIGHT".equalsIgnoreCase(a.getCode()))
                .findFirst()
                .ifPresent(badge -> pilotProfile.getAchievements().add(badge));
        }
        pilotProfileRepository.save(pilotProfile);
        return savedLog;
    }

    public PilotProfile getCurrentPilotProfile() {
        String login = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new IllegalStateException("User was not authenticated"));

        User user = userRepository.findOneByLogin(login).orElseThrow(() -> new IllegalStateException("User not found: " + login));

        return pilotProfileRepository.findByUser(user).orElseGet(() -> {
            PilotProfile newProfile = new PilotProfile();
            newProfile.setUser(user);
            newProfile.setTotalXp(0);
            newProfile.setTotalFlightHours(0.0);
            newProfile.setTotalIfrFlightHours(0.0);
            newProfile.setTotalNightFlightHours(0.0);
            newProfile.setFlightsCompleted(0);
            newProfile.setRankTier(RankTier.STUDENT);
            return pilotProfileRepository.save(newProfile);
        });
    }
}
