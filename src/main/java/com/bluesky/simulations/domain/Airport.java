package com.bluesky.simulations.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Airport.
 */
@Entity
@Table(name = "airport")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Airport implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3, max = 4)
    @Column(name = "icao_code", length = 4, nullable = false, unique = true)
    private String icaoCode;

    @Size(max = 3)
    @Column(name = "iata_code", length = 3)
    private String iataCode;

    @NotNull
    @Size(max = 100)
    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @NotNull
    @Size(max = 50)
    @Column(name = "city", length = 50, nullable = false)
    private String city;

    @NotNull
    @Size(max = 50)
    @Column(name = "country", length = 50, nullable = false)
    private String country;

    @NotNull
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Min(value = -1500)
    @Max(value = 15000)
    @Column(name = "elevation_ft")
    private Integer elevationFt;

    @NotNull
    @Min(value = 500)
    @Max(value = 20000)
    @Column(name = "longest_runway_ft", nullable = false)
    private Integer longestRunwayFt;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Airport id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcaoCode() {
        return this.icaoCode;
    }

    public Airport icaoCode(String icaoCode) {
        this.setIcaoCode(icaoCode);
        return this;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getIataCode() {
        return this.iataCode;
    }

    public Airport iataCode(String iataCode) {
        this.setIataCode(iataCode);
        return this;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return this.name;
    }

    public Airport name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return this.city;
    }

    public Airport city(String city) {
        this.setCity(city);
        return this;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return this.country;
    }

    public Airport country(String country) {
        this.setCountry(country);
        return this;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public Airport latitude(Double latitude) {
        this.setLatitude(latitude);
        return this;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public Airport longitude(Double longitude) {
        this.setLongitude(longitude);
        return this;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getElevationFt() {
        return this.elevationFt;
    }

    public Airport elevationFt(Integer elevationFt) {
        this.setElevationFt(elevationFt);
        return this;
    }

    public void setElevationFt(Integer elevationFt) {
        this.elevationFt = elevationFt;
    }

    public Integer getLongestRunwayFt() {
        return this.longestRunwayFt;
    }

    public Airport longestRunwayFt(Integer longestRunwayFt) {
        this.setLongestRunwayFt(longestRunwayFt);
        return this;
    }

    public void setLongestRunwayFt(Integer longestRunwayFt) {
        this.longestRunwayFt = longestRunwayFt;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Airport)) {
            return false;
        }
        return getId() != null && getId().equals(((Airport) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Airport{" +
            "id=" + getId() +
            ", icaoCode='" + getIcaoCode() + "'" +
            ", iataCode='" + getIataCode() + "'" +
            ", name='" + getName() + "'" +
            ", city='" + getCity() + "'" +
            ", country='" + getCountry() + "'" +
            ", latitude=" + getLatitude() +
            ", longitude=" + getLongitude() +
            ", elevationFt=" + getElevationFt() +
            ", longestRunwayFt=" + getLongestRunwayFt() +
            "}";
    }
}
