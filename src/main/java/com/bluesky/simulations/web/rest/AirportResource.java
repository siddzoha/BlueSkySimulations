package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.repository.AirportRepository;
import com.bluesky.simulations.service.AirportService;
import com.bluesky.simulations.service.dto.AirportDTO;
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
 * REST controller for managing {@link com.bluesky.simulations.domain.Airport}.
 */
@RestController
@RequestMapping("/api/airports")
public class AirportResource {

    private static final Logger LOG = LoggerFactory.getLogger(AirportResource.class);

    private static final String ENTITY_NAME = "airport";

    @Value("${jhipster.clientApp.name:blueSky}")
    private String applicationName;

    private final AirportService airportService;

    private final AirportRepository airportRepository;

    public AirportResource(AirportService airportService, AirportRepository airportRepository) {
        this.airportService = airportService;
        this.airportRepository = airportRepository;
    }

    /**
     * {@code POST  /airports} : Create a new airport.
     *
     * @param airportDTO the airportDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new airportDTO, or with status {@code 400 (Bad Request)} if the airport has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<AirportDTO> createAirport(@Valid @RequestBody AirportDTO airportDTO) throws URISyntaxException {
        LOG.debug("REST request to save Airport : {}", airportDTO);
        if (airportDTO.getId() != null) {
            throw new BadRequestAlertException("A new airport cannot already have an ID", ENTITY_NAME, "idexists");
        }
        airportDTO = airportService.save(airportDTO);
        return ResponseEntity.created(new URI("/api/airports/" + airportDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, airportDTO.getId().toString()))
            .body(airportDTO);
    }

    /**
     * {@code PUT  /airports/:id} : Updates an existing airport.
     *
     * @param id the id of the airportDTO to save.
     * @param airportDTO the airportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated airportDTO,
     * or with status {@code 400 (Bad Request)} if the airportDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the airportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<AirportDTO> updateAirport(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody AirportDTO airportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Airport : {}, {}", id, airportDTO);
        if (airportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, airportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!airportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        airportDTO = airportService.update(airportDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, airportDTO.getId().toString()))
            .body(airportDTO);
    }

    /**
     * {@code PATCH  /airports/:id} : Partial updates given fields of an existing airport, field will ignore if it is null
     *
     * @param id the id of the airportDTO to save.
     * @param airportDTO the airportDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated airportDTO,
     * or with status {@code 400 (Bad Request)} if the airportDTO is not valid,
     * or with status {@code 404 (Not Found)} if the airportDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the airportDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<AirportDTO> partialUpdateAirport(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody AirportDTO airportDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Airport partially : {}, {}", id, airportDTO);
        if (airportDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, airportDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!airportRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<AirportDTO> result = airportService.partialUpdate(airportDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, airportDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /airports} : get all the Airports.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Airports in body.
     */
    @GetMapping("")
    public ResponseEntity<List<AirportDTO>> getAllAirports(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Airports");
        Page<AirportDTO> page = airportService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /airports/:id} : get the "id" airport.
     *
     * @param id the id of the airportDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the airportDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<AirportDTO> getAirport(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Airport : {}", id);
        Optional<AirportDTO> airportDTO = airportService.findOne(id);
        return ResponseUtil.wrapOrNotFound(airportDTO);
    }

    /**
     * {@code DELETE  /airports/:id} : delete the "id" airport.
     *
     * @param id the id of the airportDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAirport(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Airport : {}", id);
        airportService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
