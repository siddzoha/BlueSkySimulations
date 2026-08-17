package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.Aircraft;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Aircraft entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {}
