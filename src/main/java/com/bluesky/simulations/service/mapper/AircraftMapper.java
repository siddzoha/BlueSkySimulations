package com.bluesky.simulations.service.mapper;

import com.bluesky.simulations.domain.Aircraft;
import com.bluesky.simulations.service.dto.AircraftDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Aircraft} and its DTO {@link AircraftDTO}.
 */
@Mapper(componentModel = "spring")
public interface AircraftMapper extends EntityMapper<AircraftDTO, Aircraft> {}
