package com.bluesky.simulations.service;

import com.bluesky.simulations.service.dto.PilotProfileDTO;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.bluesky.simulations.domain.PilotProfile}.
 */
public interface PilotProfileService {
    /**
     * Save a pilotProfile.
     *
     * @param pilotProfileDTO the entity to save.
     * @return the persisted entity.
     */
    PilotProfileDTO save(PilotProfileDTO pilotProfileDTO);

    /**
     * Updates a pilotProfile.
     *
     * @param pilotProfileDTO the entity to update.
     * @return the persisted entity.
     */
    PilotProfileDTO update(PilotProfileDTO pilotProfileDTO);

    /**
     * Partially updates a pilotProfile.
     *
     * @param pilotProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<PilotProfileDTO> partialUpdate(PilotProfileDTO pilotProfileDTO);

    /**
     * Get all the pilotProfiles.
     *
     * @return the list of entities.
     */
    List<PilotProfileDTO> findAll();

    /**
     * Get all the pilotProfiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<PilotProfileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" pilotProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<PilotProfileDTO> findOne(Long id);

    /**
     * Delete the "id" pilotProfile.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
