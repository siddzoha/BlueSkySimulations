package com.bluesky.simulations.service.dto;

import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.bluesky.simulations.domain.Aircraft} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AircraftDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 10)
    private String tailNumber;

    @NotNull
    @Size(max = 50)
    private String modelName;

    @NotNull
    @Size(max = 4)
    private String icaoType;

    @NotNull
    private AircraftCategory category;

    @NotNull
    @Min(value = 40)
    @Max(value = 600)
    private Integer cruiseSpeedKnots;

    @NotNull
    @Min(value = 100)
    @Max(value = 10000)
    private Integer maxRangeNm;

    @Min(value = 1000)
    @Max(value = 60000)
    private Integer serviceCeilingFt;

    @NotNull
    @DecimalMin(value = "1")
    @DecimalMax(value = "5000")
    private Double fuelBurnGph;

    @NotNull
    @Min(value = 500)
    @Max(value = 15000)
    private Integer minRunwayLengthFt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTailNumber() {
        return tailNumber;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getIcaoType() {
        return icaoType;
    }

    public void setIcaoType(String icaoType) {
        this.icaoType = icaoType;
    }

    public AircraftCategory getCategory() {
        return category;
    }

    public void setCategory(AircraftCategory category) {
        this.category = category;
    }

    public Integer getCruiseSpeedKnots() {
        return cruiseSpeedKnots;
    }

    public void setCruiseSpeedKnots(Integer cruiseSpeedKnots) {
        this.cruiseSpeedKnots = cruiseSpeedKnots;
    }

    public Integer getMaxRangeNm() {
        return maxRangeNm;
    }

    public void setMaxRangeNm(Integer maxRangeNm) {
        this.maxRangeNm = maxRangeNm;
    }

    public Integer getServiceCeilingFt() {
        return serviceCeilingFt;
    }

    public void setServiceCeilingFt(Integer serviceCeilingFt) {
        this.serviceCeilingFt = serviceCeilingFt;
    }

    public Double getFuelBurnGph() {
        return fuelBurnGph;
    }

    public void setFuelBurnGph(Double fuelBurnGph) {
        this.fuelBurnGph = fuelBurnGph;
    }

    public Integer getMinRunwayLengthFt() {
        return minRunwayLengthFt;
    }

    public void setMinRunwayLengthFt(Integer minRunwayLengthFt) {
        this.minRunwayLengthFt = minRunwayLengthFt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AircraftDTO)) {
            return false;
        }

        AircraftDTO aircraftDTO = (AircraftDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, aircraftDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AircraftDTO{" +
            "id=" + getId() +
            ", tailNumber='" + getTailNumber() + "'" +
            ", modelName='" + getModelName() + "'" +
            ", icaoType='" + getIcaoType() + "'" +
            ", category='" + getCategory() + "'" +
            ", cruiseSpeedKnots=" + getCruiseSpeedKnots() +
            ", maxRangeNm=" + getMaxRangeNm() +
            ", serviceCeilingFt=" + getServiceCeilingFt() +
            ", fuelBurnGph=" + getFuelBurnGph() +
            ", minRunwayLengthFt=" + getMinRunwayLengthFt() +
            "}";
    }
}
