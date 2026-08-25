package com.bluesky.simulations.domain;

import com.bluesky.simulations.domain.enumeration.RankTier;
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
 * A PilotProfile.
 */
@Entity
@Table(name = "pilot_profile")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PilotProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 0)
    @Column(name = "total_xp", nullable = false)
    private Integer totalXp;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "rank_tier", nullable = false)
    private RankTier rankTier;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_flight_hours", nullable = false)
    private Double totalFlightHours;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_night_flight_hours", nullable = false)
    private Double totalNightFlightHours;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "total_ifr_flight_hours", nullable = false)
    private Double totalIfrFlightHours;

    @NotNull
    @Min(value = 0)
    @Column(name = "flights_completed", nullable = false)
    private Integer flightsCompleted;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_pilot_profile__achievements",
        joinColumns = @JoinColumn(name = "pilot_profile_id"),
        inverseJoinColumns = @JoinColumn(name = "achievements_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "pilotses" }, allowSetters = true)
    private Set<Achievement> achievementses = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public PilotProfile id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTotalXp() {
        return this.totalXp;
    }

    public PilotProfile totalXp(Integer totalXp) {
        this.setTotalXp(totalXp);
        return this;
    }

    public void setTotalXp(Integer totalXp) {
        this.totalXp = totalXp;
    }

    public RankTier getRankTier() {
        return this.rankTier;
    }

    public PilotProfile rankTier(RankTier rankTier) {
        this.setRankTier(rankTier);
        return this;
    }

    public void setRankTier(RankTier rankTier) {
        this.rankTier = rankTier;
    }

    public Double getTotalFlightHours() {
        return this.totalFlightHours;
    }

    public PilotProfile totalFlightHours(Double totalFlightHours) {
        this.setTotalFlightHours(totalFlightHours);
        return this;
    }

    public void setTotalFlightHours(Double totalFlightHours) {
        this.totalFlightHours = totalFlightHours;
    }

    public Double getTotalNightFlightHours() {
        return this.totalNightFlightHours;
    }

    public PilotProfile totalNightFlightHours(Double totalNightFlightHours) {
        this.setTotalNightFlightHours(totalNightFlightHours);
        return this;
    }

    public void setTotalNightFlightHours(Double totalNightFlightHours) {
        this.totalNightFlightHours = totalNightFlightHours;
    }

    public Double getTotalIfrFlightHours() {
        return this.totalIfrFlightHours;
    }

    public PilotProfile totalIfrFlightHours(Double totalIfrFlightHours) {
        this.setTotalIfrFlightHours(totalIfrFlightHours);
        return this;
    }

    public void setTotalIfrFlightHours(Double totalIfrFlightHours) {
        this.totalIfrFlightHours = totalIfrFlightHours;
    }

    public Integer getFlightsCompleted() {
        return this.flightsCompleted;
    }

    public PilotProfile flightsCompleted(Integer flightsCompleted) {
        this.setFlightsCompleted(flightsCompleted);
        return this;
    }

    public void setFlightsCompleted(Integer flightsCompleted) {
        this.flightsCompleted = flightsCompleted;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public PilotProfile user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Achievement> getAchievements() {
        return this.achievementses;
    }

    public Set<Achievement> getAchievementses() {
        return this.achievementses;
    }

    public void setAchievements(Set<Achievement> achievements) {
        this.achievementses = achievements;
    }

    public void setAchievementses(Set<Achievement> achievementses) {
        this.achievementses = achievementses;
    }

    public PilotProfile achievements(Set<Achievement> achievements) {
        this.setAchievements(achievements);
        return this;
    }

    public PilotProfile addachievements(Achievement achievement) {
        this.achievementses.add(achievement);
        return this;
    }

    public PilotProfile removeachievements(Achievement achievement) {
        this.achievementses.remove(achievement);
        return this;
    }

    public PilotProfile removeAchievements(Achievement achievement) {
        this.achievementses.remove(achievement);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PilotProfile)) {
            return false;
        }
        return getId() != null && getId().equals(((PilotProfile) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PilotProfile{" +
            "id=" + getId() +
            ", totalXp=" + getTotalXp() +
            ", rankTier='" + getRankTier() + "'" +
            ", totalFlightHours=" + getTotalFlightHours() +
            ", totalNightFlightHours=" + getTotalNightFlightHours() +
            ", totalIfrFlightHours=" + getTotalIfrFlightHours() +
            ", flightsCompleted=" + getFlightsCompleted() +
            "}";
    }
}
