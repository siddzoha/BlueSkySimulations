package com.bluesky.simulations.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.bluesky.simulations.domain.Airport} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class AirportDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(min = 3, max = 4)
    private String icaoCode;

    @Size(max = 3)
    private String iataCode;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 50)
    private String city;

    @NotNull
    @Size(max = 50)
    private String country;

    @NotNull
    @DecimalMin(value = "-90")
    @DecimalMax(value = "90")
    private Double latitude;

    @NotNull
    @DecimalMin(value = "-180")
    @DecimalMax(value = "180")
    private Double longitude;

    @Min(value = -1500)
    @Max(value = 15000)
    private Integer elevationFt;

    @NotNull
    @Min(value = 500)
    @Max(value = 20000)
    private Integer longestRunwayFt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIcaoCode() {
        return icaoCode;
    }

    public void setIcaoCode(String icaoCode) {
        this.icaoCode = icaoCode;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getElevationFt() {
        return elevationFt;
    }

    public void setElevationFt(Integer elevationFt) {
        this.elevationFt = elevationFt;
    }

    public Integer getLongestRunwayFt() {
        return longestRunwayFt;
    }

    public void setLongestRunwayFt(Integer longestRunwayFt) {
        this.longestRunwayFt = longestRunwayFt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AirportDTO)) {
            return false;
        }

        AirportDTO airportDTO = (AirportDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, airportDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AirportDTO{" +
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
