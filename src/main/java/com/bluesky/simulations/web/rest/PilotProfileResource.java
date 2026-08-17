package com.bluesky.simulations.web.rest;

import com.bluesky.simulations.repository.PilotProfileRepository;
import com.bluesky.simulations.service.PilotProfileService;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
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
 * REST controller for managing {@link com.bluesky.simulations.domain.PilotProfile}.
 */
@RestController
@RequestMapping("/api/pilot-profiles")
public class PilotProfileResource {

    private static final Logger LOG = LoggerFactory.getLogger(PilotProfileResource.class);

    private static final String ENTITY_NAME = "pilotProfile";

    @Value("${jhipster.clientApp.name:blueSky}")
    private String applicationName;

    private final PilotProfileService pilotProfileService;

    private final PilotProfileRepository pilotProfileRepository;

    public PilotProfileResource(PilotProfileService pilotProfileService, PilotProfileRepository pilotProfileRepository) {
        this.pilotProfileService = pilotProfileService;
        this.pilotProfileRepository = pilotProfileRepository;
    }

    /**
     * {@code POST  /pilot-profiles} : Create a new pilotProfile.
     *
     * @param pilotProfileDTO the pilotProfileDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pilotProfileDTO, or with status {@code 400 (Bad Request)} if the pilotProfile has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PilotProfileDTO> createPilotProfile(@Valid @RequestBody PilotProfileDTO pilotProfileDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save PilotProfile : {}", pilotProfileDTO);
        if (pilotProfileDTO.getId() != null) {
            throw new BadRequestAlertException("A new pilotProfile cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pilotProfileDTO = pilotProfileService.save(pilotProfileDTO);
        return ResponseEntity.created(new URI("/api/pilot-profiles/" + pilotProfileDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, pilotProfileDTO.getId().toString()))
            .body(pilotProfileDTO);
    }

    /**
     * {@code PUT  /pilot-profiles/:id} : Updates an existing pilotProfile.
     *
     * @param id the id of the pilotProfileDTO to save.
     * @param pilotProfileDTO the pilotProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pilotProfileDTO,
     * or with status {@code 400 (Bad Request)} if the pilotProfileDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pilotProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PilotProfileDTO> updatePilotProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PilotProfileDTO pilotProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PilotProfile : {}, {}", id, pilotProfileDTO);
        if (pilotProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pilotProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pilotProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pilotProfileDTO = pilotProfileService.update(pilotProfileDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pilotProfileDTO.getId().toString()))
            .body(pilotProfileDTO);
    }

    /**
     * {@code PATCH  /pilot-profiles/:id} : Partial updates given fields of an existing pilotProfile, field will ignore if it is null
     *
     * @param id the id of the pilotProfileDTO to save.
     * @param pilotProfileDTO the pilotProfileDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pilotProfileDTO,
     * or with status {@code 400 (Bad Request)} if the pilotProfileDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pilotProfileDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pilotProfileDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PilotProfileDTO> partialUpdatePilotProfile(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PilotProfileDTO pilotProfileDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PilotProfile partially : {}, {}", id, pilotProfileDTO);
        if (pilotProfileDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pilotProfileDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pilotProfileRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PilotProfileDTO> result = pilotProfileService.partialUpdate(pilotProfileDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, pilotProfileDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /pilot-profiles} : get all the Pilot Profiles.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of Pilot Profiles in body.
     */
    @GetMapping("")
    public List<PilotProfileDTO> getAllPilotProfiles(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all PilotProfiles");
        return pilotProfileService.findAll();
    }

    /**
     * {@code GET  /pilot-profiles/:id} : get the "id" pilotProfile.
     *
     * @param id the id of the pilotProfileDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pilotProfileDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PilotProfileDTO> getPilotProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PilotProfile : {}", id);
        Optional<PilotProfileDTO> pilotProfileDTO = pilotProfileService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pilotProfileDTO);
    }

    /**
     * {@code DELETE  /pilot-profiles/:id} : delete the "id" pilotProfile.
     *
     * @param id the id of the pilotProfileDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePilotProfile(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PilotProfile : {}", id);
        pilotProfileService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
