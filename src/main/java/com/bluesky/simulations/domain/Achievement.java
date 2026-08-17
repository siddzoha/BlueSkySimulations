package com.bluesky.simulations.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Achievement.
 */
@Entity
@Table(name = "achievement")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Achievement implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 50)
    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(max = 100)
    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @NotNull
    @Size(max = 255)
    @Column(name = "description", length = 255, nullable = false)
    private String description;

    @Size(max = 50)
    @Column(name = "badge_icon", length = 50)
    private String badgeIcon;

    @NotNull
    @Min(value = 0)
    @Column(name = "xp_reward", nullable = false)
    private Integer xpReward;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "achievementses")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "user", "achievementses" }, allowSetters = true)
    private Set<PilotProfile> pilotses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Achievement id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Achievement code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return this.title;
    }

    public Achievement title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Achievement description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBadgeIcon() {
        return this.badgeIcon;
    }

    public Achievement badgeIcon(String badgeIcon) {
        this.setBadgeIcon(badgeIcon);
        return this;
    }

    public void setBadgeIcon(String badgeIcon) {
        this.badgeIcon = badgeIcon;
    }

    public Integer getXpReward() {
        return this.xpReward;
    }

    public Achievement xpReward(Integer xpReward) {
        this.setXpReward(xpReward);
        return this;
    }

    public void setXpReward(Integer xpReward) {
        this.xpReward = xpReward;
    }

    public Set<PilotProfile> getPilotses() {
        return this.pilotses;
    }

    public void setPilotses(Set<PilotProfile> pilotProfiles) {
        if (this.pilotses != null) {
            this.pilotses.forEach(i -> i.removeAchievements(this));
        }
        if (pilotProfiles != null) {
            pilotProfiles.forEach(i -> i.addAchievements(this));
        }
        this.pilotses = pilotProfiles;
    }

    public Achievement pilotses(Set<PilotProfile> pilotProfiles) {
        this.setPilotses(pilotProfiles);
        return this;
    }

    public Achievement addPilots(PilotProfile pilotProfile) {
        this.pilotses.add(pilotProfile);
        pilotProfile.getAchievementses().add(this);
        return this;
    }

    public Achievement removePilots(PilotProfile pilotProfile) {
        this.pilotses.remove(pilotProfile);
        pilotProfile.getAchievementses().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Achievement)) {
            return false;
        }
        return getId() != null && getId().equals(((Achievement) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Achievement{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", badgeIcon='" + getBadgeIcon() + "'" +
            ", xpReward=" + getXpReward() +
            "}";
    }
}
