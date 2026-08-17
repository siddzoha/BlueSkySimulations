package com.bluesky.simulations.service.mapper;

import com.bluesky.simulations.domain.Aircraft;
import com.bluesky.simulations.domain.Airport;
import com.bluesky.simulations.domain.FlightLog;
import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.service.dto.AircraftDTO;
import com.bluesky.simulations.service.dto.AirportDTO;
import com.bluesky.simulations.service.dto.FlightLogDTO;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlightLog} and its DTO {@link FlightLogDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlightLogMapper extends EntityMapper<FlightLogDTO, FlightLog> {
    @Mapping(target = "aircraft", source = "aircraft", qualifiedByName = "aircraftModelName")
    @Mapping(target = "departureAirport", source = "departureAirport", qualifiedByName = "airportName")
    @Mapping(target = "arrivalAirport", source = "arrivalAirport", qualifiedByName = "airportName")
    @Mapping(target = "pilot", source = "pilot", qualifiedByName = "pilotProfileId")
    FlightLogDTO toDto(FlightLog s);

    @Named("aircraftModelName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "modelName", source = "modelName")
    AircraftDTO toDtoAircraftModelName(Aircraft aircraft);

    @Named("airportName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    AirportDTO toDtoAirportName(Airport airport);

    @Named("pilotProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PilotProfileDTO toDtoPilotProfileId(PilotProfile pilotProfile);
}
