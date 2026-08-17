package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.Achievement;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Achievement entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {}
