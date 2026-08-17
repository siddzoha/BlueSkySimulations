package com.bluesky.simulations.service.mapper;

import com.bluesky.simulations.domain.Achievement;
import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.domain.User;
import com.bluesky.simulations.service.dto.AchievementDTO;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import com.bluesky.simulations.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link PilotProfile} and its DTO {@link PilotProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface PilotProfileMapper extends EntityMapper<PilotProfileDTO, PilotProfile> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "achievementses", source = "achievementses", qualifiedByName = "achievementTitleSet")
    PilotProfileDTO toDto(PilotProfile s);

    @Mapping(target = "removeAchievements", ignore = true)
    PilotProfile toEntity(PilotProfileDTO pilotProfileDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("achievementTitle")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    AchievementDTO toDtoAchievementTitle(Achievement achievement);

    @Named("achievementTitleSet")
    default Set<AchievementDTO> toDtoAchievementTitleSet(Set<Achievement> achievement) {
        return achievement.stream().map(this::toDtoAchievementTitle).collect(Collectors.toSet());
    }
}
