package com.bluesky.simulations.service;

import com.bluesky.simulations.service.dto.AircraftDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.bluesky.simulations.domain.Aircraft}.
 */
public interface AircraftService {
    /**
     * Save a aircraft.
     *
     * @param aircraftDTO the entity to save.
     * @return the persisted entity.
     */
    AircraftDTO save(AircraftDTO aircraftDTO);

    /**
     * Updates a aircraft.
     *
     * @param aircraftDTO the entity to update.
     * @return the persisted entity.
     */
    AircraftDTO update(AircraftDTO aircraftDTO);

    /**
     * Partially updates a aircraft.
     *
     * @param aircraftDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AircraftDTO> partialUpdate(AircraftDTO aircraftDTO);

    /**
     * Get all the aircrafts.
     *
     * @return the list of entities.
     */
    List<AircraftDTO> findAll();

    /**
     * Get the "id" aircraft.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AircraftDTO> findOne(Long id);

    /**
     * Delete the "id" aircraft.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
