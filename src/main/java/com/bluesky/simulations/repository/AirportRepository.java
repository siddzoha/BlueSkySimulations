package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.Airport;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Airport entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AirportRepository extends JpaRepository<Airport, Long> {}
