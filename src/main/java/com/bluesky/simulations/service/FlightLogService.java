package com.bluesky.simulations.service;

import com.bluesky.simulations.service.dto.FlightLogDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.bluesky.simulations.domain.FlightLog}.
 */
public interface FlightLogService {
    /**
     * Save a flightLog.
     *
     * @param flightLogDTO the entity to save.
     * @return the persisted entity.
     */
    FlightLogDTO save(FlightLogDTO flightLogDTO);

    /**
     * Updates a flightLog.
     *
     * @param flightLogDTO the entity to update.
     * @return the persisted entity.
     */
    FlightLogDTO update(FlightLogDTO flightLogDTO);

    /**
     * Partially updates a flightLog.
     *
     * @param flightLogDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<FlightLogDTO> partialUpdate(FlightLogDTO flightLogDTO);

    /**
     * Get all the flightLogs.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FlightLogDTO> findAll(Pageable pageable);

    /**
     * Get all the flightLogs with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<FlightLogDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" flightLog.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<FlightLogDTO> findOne(Long id);

    /**
     * Delete the "id" flightLog.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
