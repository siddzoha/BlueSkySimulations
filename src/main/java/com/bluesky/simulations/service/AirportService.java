package com.bluesky.simulations.service;

import com.bluesky.simulations.service.dto.AirportDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.bluesky.simulations.domain.Airport}.
 */
public interface AirportService {
    /**
     * Save a airport.
     *
     * @param airportDTO the entity to save.
     * @return the persisted entity.
     */
    AirportDTO save(AirportDTO airportDTO);

    /**
     * Updates a airport.
     *
     * @param airportDTO the entity to update.
     * @return the persisted entity.
     */
    AirportDTO update(AirportDTO airportDTO);

    /**
     * Partially updates a airport.
     *
     * @param airportDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<AirportDTO> partialUpdate(AirportDTO airportDTO);

    /**
     * Get all the airports.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<AirportDTO> findAll(Pageable pageable);

    /**
     * Get the "id" airport.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<AirportDTO> findOne(Long id);

    /**
     * Delete the "id" airport.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
