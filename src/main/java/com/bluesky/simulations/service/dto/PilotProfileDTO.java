package com.bluesky.simulations.service.dto;

import com.bluesky.simulations.domain.enumeration.RankTier;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.bluesky.simulations.domain.PilotProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PilotProfileDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 0)
    private Integer totalXp;

    @NotNull
    private RankTier rankTier;

    @NotNull
    @DecimalMin(value = "0")
    private Double totalFlightHours;

    @NotNull
    @DecimalMin(value = "0")
    private Double totalNightFlightHours;

    @NotNull
    @DecimalMin(value = "0")
    private Double totalIfrFlightHours;

    @NotNull
    @Min(value = 0)
    private Integer flightsCompleted;

    private UserDTO user;

    private Set<AchievementDTO> achievementses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalXp() {
        return totalXp;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public RankTier getRankTier() {
        return rankTier;
    }

    public void setRankTier(RankTier rankTier) {
        this.rankTier = rankTier;
    }

    public Double getTotalFlightHours() {
        return totalFlightHours;
    }

    public void setTotalFlightHours(Double totalFlightHours) {
        this.totalFlightHours = totalFlightHours;
    }

    public Double getTotalNightFlightHours() {
        return totalNightFlightHours;
    }

    public void setTotalNightFlightHours(Double totalNightFlightHours) {
        this.totalNightFlightHours = totalNightFlightHours;
    }

    public Double getTotalIfrFlightHours() {
        return totalIfrFlightHours;
    }

    public void setTotalIfrFlightHours(Double totalIfrFlightHours) {
        this.totalIfrFlightHours = totalIfrFlightHours;
    }

    public Integer getFlightsCompleted() {
        return flightsCompleted;
    }

    public void setFlightsCompleted(Integer flightsCompleted) {
        this.flightsCompleted = flightsCompleted;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<AchievementDTO> getAchievementses() {
        return achievementses;
    }

    public Set<AchievementDTO> getAchievements() {
        return achievementses;
    }

    public void setAchievementses(Set<AchievementDTO> achievementses) {
        this.achievementses = achievementses;
    }

    public void setAchievements(Set<AchievementDTO> achievements) {
        this.achievementses = achievements;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PilotProfileDTO)) {
            return false;
        }

        PilotProfileDTO pilotProfileDTO = (PilotProfileDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pilotProfileDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PilotProfileDTO{" +
            "id=" + getId() +
            ", totalXp=" + getTotalXp() +
            ", rankTier='" + getRankTier() + "'" +
            ", totalFlightHours=" + getTotalFlightHours() +
            ", totalNightFlightHours=" + getTotalNightFlightHours() +
            ", totalIfrFlightHours=" + getTotalIfrFlightHours() +
            ", flightsCompleted=" + getFlightsCompleted() +
            ", user=" + getUser() +
            ", achievementses=" + getAchievementses() +
            "}";
    }
}
