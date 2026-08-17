package com.bluesky.simulations.service;

import com.bluesky.simulations.service.dto.AchievementDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.bluesky.simulations.domain.Achievement}.
 */
public interface AchievementService {
    /**
     * Save a achievement.
     *
     * @param achievementDTO the entity to save.
     * @return the persisted entity.
     */
    AchievementDTO save(AchievementDTO achievementDTO);

    /**
     * Updates a achievement.
     *
     * @param achievementDTO the entity to update.
     * @return the persisted entity.
     */
    AchievementDTO update(AchievementDTO achievementDTO);

    /**
     * Partially updates a achievement.
     *
     * @param achievementDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AchievementDTO> partialUpdate(AchievementDTO achievementDTO);

    /**
     * Get all the achievements.
     *
     * @return the list of entities.
     */
    List<AchievementDTO> findAll();

    /**
     * Get the "id" achievement.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AchievementDTO> findOne(Long id);

    /**
     * Delete the "id" achievement.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
