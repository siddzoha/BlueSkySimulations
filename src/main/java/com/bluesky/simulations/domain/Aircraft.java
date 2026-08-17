package com.bluesky.simulations.domain;

import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Aircraft.
 */
@Entity
@Table(name = "aircraft")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Aircraft implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(max = 10)
    @Column(name = "tail_number", length = 10, nullable = false)
    private String tailNumber;

    @NotNull
    @Size(max = 50)
    @Column(name = "model_name", length = 50, nullable = false)
    private String modelName;

    @NotNull
    @Size(max = 4)
    @Column(name = "icao_type", length = 4, nullable = false)
    private String icaoType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private AircraftCategory category;

    @NotNull
    @Min(value = 40)
    @Max(value = 600)
    @Column(name = "cruise_speed_knots", nullable = false)
    private Integer cruiseSpeedKnots;

    @NotNull
    @Min(value = 100)
    @Max(value = 10000)
    @Column(name = "max_range_nm", nullable = false)
    private Integer maxRangeNm;

    @Min(value = 1000)
    @Max(value = 60000)
    @Column(name = "service_ceiling_ft")
    private Integer serviceCeilingFt;

    @NotNull
    @DecimalMin(value = "1")
    @DecimalMax(value = "5000")
    @Column(name = "fuel_burn_gph", nullable = false)
    private Double fuelBurnGph;

    @NotNull
    @Min(value = 500)
    @Max(value = 15000)
    @Column(name = "min_runway_length_ft", nullable = false)
    private Integer minRunwayLengthFt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Aircraft id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTailNumber() {
        return this.tailNumber;
    }

    public Aircraft tailNumber(String tailNumber) {
        this.setTailNumber(tailNumber);
        return this;
    }

    public void setTailNumber(String tailNumber) {
        this.tailNumber = tailNumber;
    }

    public String getModelName() {
        return this.modelName;
    }

    public Aircraft modelName(String modelName) {
        this.setModelName(modelName);
        return this;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getIcaoType() {
        return this.icaoType;
    }

    public Aircraft icaoType(String icaoType) {
        this.setIcaoType(icaoType);
        return this;
    }

    public void setIcaoType(String icaoType) {
        this.icaoType = icaoType;
    }

    public AircraftCategory getCategory() {
        return this.category;
    }

    public Aircraft category(AircraftCategory category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(AircraftCategory category) {
        this.category = category;
    }

    public Integer getCruiseSpeedKnots() {
        return this.cruiseSpeedKnots;
    }

    public Aircraft cruiseSpeedKnots(Integer cruiseSpeedKnots) {
        this.setCruiseSpeedKnots(cruiseSpeedKnots);
        return this;
    }

    public void setCruiseSpeedKnots(Integer cruiseSpeedKnots) {
        this.cruiseSpeedKnots = cruiseSpeedKnots;
    }

    public Integer getMaxRangeNm() {
        return this.maxRangeNm;
    }

    public Aircraft maxRangeNm(Integer maxRangeNm) {
        this.setMaxRangeNm(maxRangeNm);
        return this;
    }

    public void setMaxRangeNm(Integer maxRangeNm) {
        this.maxRangeNm = maxRangeNm;
    }

    public Integer getServiceCeilingFt() {
        return this.serviceCeilingFt;
    }

    public Aircraft serviceCeilingFt(Integer serviceCeilingFt) {
        this.setServiceCeilingFt(serviceCeilingFt);
        return this;
    }

    public void setServiceCeilingFt(Integer serviceCeilingFt) {
        this.serviceCeilingFt = serviceCeilingFt;
    }

    public Double getFuelBurnGph() {
        return this.fuelBurnGph;
    }

    public Aircraft fuelBurnGph(Double fuelBurnGph) {
        this.setFuelBurnGph(fuelBurnGph);
        return this;
    }

    public void setFuelBurnGph(Double fuelBurnGph) {
        this.fuelBurnGph = fuelBurnGph;
    }

    public Integer getMinRunwayLengthFt() {
        return this.minRunwayLengthFt;
    }

    public Aircraft minRunwayLengthFt(Integer minRunwayLengthFt) {
        this.setMinRunwayLengthFt(minRunwayLengthFt);
        return this;
    }

    public void setMinRunwayLengthFt(Integer minRunwayLengthFt) {
        this.minRunwayLengthFt = minRunwayLengthFt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Aircraft)) {
            return false;
        }
        return getId() != null && getId().equals(((Aircraft) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Aircraft{" +
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
