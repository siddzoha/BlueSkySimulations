package com.bluesky.simulations.service.mapper;

import com.bluesky.simulations.domain.Airport;
import com.bluesky.simulations.service.dto.AirportDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Airport} and its DTO {@link AirportDTO}.
 */
@Mapper(componentModel = "spring")
public interface AirportMapper extends EntityMapper<AirportDTO, Airport> {}
