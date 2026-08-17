package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.repository.FlightLogRepository;
import com.bluesky.simulations.service.FlightLogService;
import com.bluesky.simulations.service.dto.FlightLogDTO;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.bluesky.simulations.domain.FlightLog}.
 */
@RestController
@RequestMapping("/api/flight-logs")
public class FlightLogResource {

    private static final Logger LOG = LoggerFactory.getLogger(FlightLogResource.class);

    private static final String ENTITY_NAME = "flightLog";

    @Value("${jhipster.clientApp.name:blueSky}")
    private String applicationName;

    private final FlightLogService flightLogService;

    private final FlightLogRepository flightLogRepository;

    public FlightLogResource(FlightLogService flightLogService, FlightLogRepository flightLogRepository) {
        this.flightLogService = flightLogService;
        this.flightLogRepository = flightLogRepository;
    }

    /**
     * {@code POST  /flight-logs} : Create a new flightLog.
     *
     * @param flightLogDTO the flightLogDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new flightLogDTO, or with status {@code 400 (Bad Request)} if the flightLog has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<FlightLogDTO> createFlightLog(@Valid @RequestBody FlightLogDTO flightLogDTO) throws URISyntaxException {
        LOG.debug("REST request to save FlightLog : {}", flightLogDTO);
        if (flightLogDTO.getId() != null) {
            throw new BadRequestAlertException("A new flightLog cannot already have an ID", ENTITY_NAME, "idexists");
        }
        flightLogDTO = flightLogService.save(flightLogDTO);
        return ResponseEntity.created(new URI("/api/flight-logs/" + flightLogDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, flightLogDTO.getId().toString()))
            .body(flightLogDTO);
    }

    /**
     * {@code PUT  /flight-logs/:id} : Updates an existing flightLog.
     *
     * @param id the id of the flightLogDTO to save.
     * @param flightLogDTO the flightLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flightLogDTO,
     * or with status {@code 400 (Bad Request)} if the flightLogDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the flightLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<FlightLogDTO> updateFlightLog(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody FlightLogDTO flightLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update FlightLog : {}, {}", id, flightLogDTO);
        if (flightLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flightLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flightLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        flightLogDTO = flightLogService.update(flightLogDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flightLogDTO.getId().toString()))
            .body(flightLogDTO);
    }

    /**
     * {@code PATCH  /flight-logs/:id} : Partial updates given fields of an existing flightLog, field will ignore if it is null
     *
     * @param id the id of the flightLogDTO to save.
     * @param flightLogDTO the flightLogDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated flightLogDTO,
     * or with status {@code 400 (Bad Request)} if the flightLogDTO is not valid,
     * or with status {@code 404 (Not Found)} if the flightLogDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the flightLogDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<FlightLogDTO> partialUpdateFlightLog(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody FlightLogDTO flightLogDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update FlightLog partially : {}, {}", id, flightLogDTO);
        if (flightLogDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, flightLogDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!flightLogRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<FlightLogDTO> result = flightLogService.partialUpdate(flightLogDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, flightLogDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /flight-logs} : get all the Flight Logs.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Flight Logs in body.
     */
    @GetMapping("")
    public ResponseEntity<List<FlightLogDTO>> getAllFlightLogs(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of FlightLogs");
        Page<FlightLogDTO> page;
        if (eagerload) {
            page = flightLogService.findAllWithEagerRelationships(pageable);
        } else {
            page = flightLogService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /flight-logs/:id} : get the "id" flightLog.
     *
     * @param id the id of the flightLogDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the flightLogDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<FlightLogDTO> getFlightLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to get FlightLog : {}", id);
        Optional<FlightLogDTO> flightLogDTO = flightLogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(flightLogDTO);
    }

    /**
     * {@code DELETE  /flight-logs/:id} : delete the "id" flightLog.
     *
     * @param id the id of the flightLogDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlightLog(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete FlightLog : {}", id);
        flightLogService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
