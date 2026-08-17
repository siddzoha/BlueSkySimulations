package com.bluesky.simulations.service.dto;

import com.bluesky.simulations.domain.enumeration.FlightRules;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.bluesky.simulations.domain.FlightLog} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlightLogDTO implements Serializable {

    private Long id;

    @NotNull
    private Instant departureTime;

    @NotNull
    private Instant arrivalTime;

    @NotNull
    @DecimalMin(value = "0")
    @DecimalMax(value = "30")
    private Double durationHours;

    @NotNull
    @Min(value = 1)
    @Max(value = 15000)
    private Integer distanceNm;

    @Min(value = 1000)
    @Max(value = 60000)
    private Integer cruiseAltitudeFt;

    @DecimalMin(value = "0")
    private Double fuelUsedGallons;

    @Min(value = 0)
    private Integer xpEarned;

    @NotNull
    private FlightRules flightRules;

    @Size(max = 500)
    private String remarks;

    private AircraftDTO aircraft;

    private AirportDTO departureAirport;

    private AirportDTO arrivalAirport;

    private PilotProfileDTO pilot;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(Instant departureTime) {
        this.departureTime = departureTime;
    }

    public Instant getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Instant arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Double getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Double durationHours) {
        this.durationHours = durationHours;
    }

    public Integer getDistanceNm() {
        return distanceNm;
    }

    public void setDistanceNm(Integer distanceNm) {
        this.distanceNm = distanceNm;
    }

    public Integer getCruiseAltitudeFt() {
        return cruiseAltitudeFt;
    }

    public void setCruiseAltitudeFt(Integer cruiseAltitudeFt) {
        this.cruiseAltitudeFt = cruiseAltitudeFt;
    }

    public Double getFuelUsedGallons() {
        return fuelUsedGallons;
    }

    public void setFuelUsedGallons(Double fuelUsedGallons) {
        this.fuelUsedGallons = fuelUsedGallons;
    }

    public Integer getXpEarned() {
        return xpEarned;
    }

    public void setXpEarned(Integer xpEarned) {
        this.xpEarned = xpEarned;
    }

    public FlightRules getFlightRules() {
        return flightRules;
    }

    public void setFlightRules(FlightRules flightRules) {
        this.flightRules = flightRules;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public AircraftDTO getAircraft() {
        return aircraft;
    }

    public void setAircraft(AircraftDTO aircraft) {
        this.aircraft = aircraft;
    }

    public AirportDTO getDepartureAirport() {
        return departureAirport;
    }

    public void setDepartureAirport(AirportDTO departureAirport) {
        this.departureAirport = departureAirport;
    }

    public AirportDTO getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(AirportDTO arrivalAirport) {
        this.arrivalAirport = arrivalAirport;
    }

    public PilotProfileDTO getPilot() {
        return pilot;
    }

    public void setPilot(PilotProfileDTO pilot) {
        this.pilot = pilot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlightLogDTO)) {
            return false;
        }

        FlightLogDTO flightLogDTO = (FlightLogDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flightLogDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlightLogDTO{" +
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
            ", aircraft=" + getAircraft() +
            ", departureAirport=" + getDepartureAirport() +
            ", arrivalAirport=" + getArrivalAirport() +
            ", pilot=" + getPilot() +
            "}";
    }
}
