package com.bluesky.simulations.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.bluesky.simulations.domain.Achievement} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AchievementDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @NotNull
    @Size(max = 100)
    private String title;

    @NotNull
    @Size(max = 255)
    private String description;

    @Size(max = 50)
    private String badgeIcon;

    @NotNull
    @Min(value = 0)
    private Integer xpReward;

    private Set<PilotProfileDTO> pilotses = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeIcon() {
        return badgeIcon;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public Integer getXpReward() {
        return xpReward;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public Set<PilotProfileDTO> getPilotses() {
        return pilotses;
    }

    public void setPilotses(Set<PilotProfileDTO> pilotses) {
        this.pilotses = pilotses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AchievementDTO)) {
            return false;
        }

        AchievementDTO achievementDTO = (AchievementDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, achievementDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AchievementDTO{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", badgeIcon='" + getBadgeIcon() + "'" +
            ", xpReward=" + getXpReward() +
            ", pilotses=" + getPilotses() +
            "}";
    }
}
