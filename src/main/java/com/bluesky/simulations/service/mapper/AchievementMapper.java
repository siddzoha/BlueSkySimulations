package com.bluesky.simulations.service.mapper;

import com.bluesky.simulations.domain.Achievement;
import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.service.dto.AchievementDTO;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Achievement} and its DTO {@link AchievementDTO}.
 */
@Mapper(componentModel = "spring")
public interface AchievementMapper extends EntityMapper<AchievementDTO, Achievement> {
    @Mapping(target = "pilotses", source = "pilotses", qualifiedByName = "pilotProfileIdSet")
    AchievementDTO toDto(Achievement s);

    @Mapping(target = "pilotses", ignore = true)
    @Mapping(target = "removePilots", ignore = true)
    Achievement toEntity(AchievementDTO achievementDTO);

    @Named("pilotProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    PilotProfileDTO toDtoPilotProfileId(PilotProfile pilotProfile);

    @Named("pilotProfileIdSet")
    default Set<PilotProfileDTO> toDtoPilotProfileIdSet(Set<PilotProfile> pilotProfile) {
        return pilotProfile.stream().map(this::toDtoPilotProfileId).collect(Collectors.toSet());
    }
}
