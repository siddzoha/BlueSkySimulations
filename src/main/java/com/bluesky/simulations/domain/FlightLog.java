package com.bluesky.simulations.domain;

import com.bluesky.simulations.domain.enumeration.FlightRules;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A FlightLog.
 */
@Entity
@Table(name = "flight_log")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlightLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "departure_time", nullable = false)
    private Instant departureTime;

    @NotNull
    @Column(name = "arrival_time", nullable = false)
    private Instant arrivalTime;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "30")
    @Column(name = "duration_hours", nullable = false)
    private Double durationHours;

    @NotNull
    @Min(value = 1)
    @Max(value = 15000)
    @Column(name = "distance_nm", nullable = false)
    private Integer distanceNm;

    @Min(value = 1000)
    @Max(value = 60000)
    @Column(name = "cruise_altitude_ft")
    private Integer cruiseAltitudeFt;

    @DecimalMin(value = "0")
    @Column(name = "fuel_used_gallons")
    private Double fuelUsedGallons;

    @Min(value = 0)
    @Column(name = "xp_earned")
    private Integer xpEarned;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "flight_rules", nullable = false)
    private FlightRules flightRules;

    @Size(max = 500)
    @Column(name = "remarks", length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    private Aircraft aircraft;

    @ManyToOne(fetch = FetchType.LAZY)
    private Airport departureAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    private Airport arrivalAirport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user", "achievementses" }, allowSetters = true)
    private PilotProfile pilot;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public FlightLog id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDepartureTime() {
        return this.departureTime;
    }

    public FlightLog departureTime(Instant departureTime) {
        this.setDepartureTime(departureTime);
        return this;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getArrivalTime() {
        return this.arrivalTime;
    }

    public FlightLog arrivalTime(Instant arrivalTime) {
        this.setArrivalTime(arrivalTime);
        return this;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Double getDurationHours() {
        return this.durationHours;
    }

    public FlightLog durationHours(Double durationHours) {
        this.setDurationHours(durationHours);
        return this;
    }

    public void setDurationHours(Double durationHours) {
        this.durationHours = durationHours;
    }

    public Integer getDistanceNm() {
        return this.distanceNm;
    }

    public FlightLog distanceNm(Integer distanceNm) {
        this.setDistanceNm(distanceNm);
        return this;
    }

    public void setDistanceNm(Integer distanceNm) {
        this.distanceNm = distanceNm;
    }

    public Integer getCruiseAltitudeFt() {
        return this.cruiseAltitudeFt;
    }

    public FlightLog cruiseAltitudeFt(Integer cruiseAltitudeFt) {
        this.setCruiseAltitudeFt(cruiseAltitudeFt);
        return this;
    }

    public void setCruiseAltitudeFt(Integer cruiseAltitudeFt) {
        this.cruiseAltitudeFt = cruiseAltitudeFt;
    }

    public Double getFuelUsedGallons() {
        return this.fuelUsedGallons;
    }

    public FlightLog fuelUsedGallons(Double fuelUsedGallons) {
        this.setFuelUsedGallons(fuelUsedGallons);
        return this;
    }

    public void setFuelUsedGallons(Double fuelUsedGallons) {
        this.fuelUsedGallons = fuelUsedGallons;
    }

    public Integer getXpEarned() {
        return this.xpEarned;
    }

    public FlightLog xpEarned(Integer xpEarned) {
        this.setXpEarned(xpEarned);
        return this;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public FlightRules getFlightRules() {
        return this.flightRules;
    }

    public FlightLog flightRules(FlightRules flightRules) {
        this.setFlightRules(flightRules);
        return this;
    }

    public void setFlightRules(FlightRules flightRules) {
        this.flightRules = flightRules;
    }

    public String getRemarks() {
        return this.remarks;
    }

    public FlightLog remarks(String remarks) {
        this.setRemarks(remarks);
        return this;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Aircraft getAircraft() {
        return this.aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public FlightLog aircraft(Aircraft aircraft) {
        this.setAircraft(aircraft);
        return this;
    }

    public Airport getDepartureAirport() {
        return this.departureAirport;
    }

    public void setDepartureAirport(Airport airport) {
        this.departureAirport = airport;
    }

    public FlightLog departureAirport(Airport airport) {
        this.setDepartureAirport(airport);
        return this;
    }

    public Airport getArrivalAirport() {
        return this.arrivalAirport;
    }

    public void setArrivalAirport(Airport airport) {
        this.arrivalAirport = airport;
    }

    public FlightLog arrivalAirport(Airport airport) {
        this.setArrivalAirport(airport);
        return this;
    }

    public PilotProfile getPilot() {
        return this.pilot;
    }

    public void setPilot(PilotProfile pilotProfile) {
        this.pilot = pilotProfile;
    }

    public FlightLog pilot(PilotProfile pilotProfile) {
        this.setPilot(pilotProfile);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlightLog)) {
            return false;
        }
        return getId() != null && getId().equals(((FlightLog) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlightLog{" +
            "id=" + getId() +
            ", departureTime='" + getDepartureTime() + "'" +
            ", arrivalTime='" + getArrivalTime() + "'" +
            ", durationHours=" + getDurationHours() +
            ", distanceNm=" + getDistanceNm() +
            ", cruiseAltitudeFt=" + getCruiseAltitudeFt() +
            ", fuelUsedGallons=" + getFuelUsedGallons() +
            ", xpEarned=" + getXpEarned() +
            ", flightRules='" + getFlightRules() + "'" +
            ", remarks='" + getRemarks() + "'" +
            "}";
    }
}
