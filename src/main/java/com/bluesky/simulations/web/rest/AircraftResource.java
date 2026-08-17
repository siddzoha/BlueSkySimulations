package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.repository.AircraftRepository;
import com.bluesky.simulations.service.AircraftService;
import com.bluesky.simulations.service.dto.AircraftDTO;
import com.bluesky.simulations.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bluesky.simulations.domain.Aircraft}.
 */
@RestController
@RequestMapping("/api/aircraft")
public class AircraftResource {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftResource.class);

    private static final String ENTITY_NAME = "aircraft";

    @Value("${jhipster.clientApp.name:blueSky}")
    private String applicationName;

    private final AircraftService aircraftService;

    private final AircraftRepository aircraftRepository;

    public AircraftResource(AircraftService aircraftService, AircraftRepository aircraftRepository) {
        this.aircraftService = aircraftService;
        this.aircraftRepository = aircraftRepository;
    }

    /**
     * {@code POST  /aircraft} : Create a new aircraft.
     *
     * @param aircraftDTO the aircraftDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new aircraftDTO, or with status {@code 400 (Bad Request)} if the aircraft has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AircraftDTO> createAircraft(@Valid @RequestBody AircraftDTO aircraftDTO) throws URISyntaxException {
        LOG.debug("REST request to save Aircraft : {}", aircraftDTO);
        if (aircraftDTO.getId() != null) {
            throw new BadRequestAlertException("A new aircraft cannot already have an ID", ENTITY_NAME, "idexists");
        }
        aircraftDTO = aircraftService.save(aircraftDTO);
        return ResponseEntity.created(new URI("/api/aircraft/" + aircraftDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, aircraftDTO.getId().toString()))
            .body(aircraftDTO);
    }

    /**
     * {@code PUT  /aircraft/:id} : Updates an existing aircraft.
     *
     * @param id the id of the aircraftDTO to save.
     * @param aircraftDTO the aircraftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aircraftDTO,
     * or with status {@code 400 (Bad Request)} if the aircraftDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the aircraftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AircraftDTO> updateAircraft(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AircraftDTO aircraftDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Aircraft : {}, {}", id, aircraftDTO);
        if (aircraftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aircraftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aircraftRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        aircraftDTO = aircraftService.update(aircraftDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aircraftDTO.getId().toString()))
            .body(aircraftDTO);
    }

    /**
     * {@code PATCH  /aircraft/:id} : Partial updates given fields of an existing aircraft, field will ignore if it is null
     *
     * @param id the id of the aircraftDTO to save.
     * @param aircraftDTO the aircraftDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated aircraftDTO,
     * or with status {@code 400 (Bad Request)} if the aircraftDTO is not valid,
     * or with status {@code 404 (Not Found)} if the aircraftDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the aircraftDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AircraftDTO> partialUpdateAircraft(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AircraftDTO aircraftDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Aircraft partially : {}, {}", id, aircraftDTO);
        if (aircraftDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, aircraftDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!aircraftRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AircraftDTO> result = aircraftService.partialUpdate(aircraftDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, aircraftDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /aircraft} : get all the Aircraft.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Aircraft in body.
     */
    @GetMapping("")
    public List<AircraftDTO> getAllAircrafts() {
        LOG.debug("REST request to get all Aircrafts");
        return aircraftService.findAll();
    }

    /**
     * {@code GET  /aircraft/:id} : get the "id" aircraft.
     *
     * @param id the id of the aircraftDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the aircraftDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AircraftDTO> getAircraft(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Aircraft : {}", id);
        Optional<AircraftDTO> aircraftDTO = aircraftService.findOne(id);
        return ResponseUtil.wrapOrNotFound(aircraftDTO);
    }

    /**
     * {@code DELETE  /aircraft/:id} : delete the "id" aircraft.
     *
     * @param id the id of the aircraftDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAircraft(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Aircraft : {}", id);
        aircraftService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
