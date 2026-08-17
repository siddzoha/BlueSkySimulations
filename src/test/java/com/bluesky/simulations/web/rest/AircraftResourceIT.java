package com.bluesky.simulations.web.rest;

import static com.bluesky.simulations.domain.AircraftAsserts.*;
import static com.bluesky.simulations.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bluesky.simulations.IntegrationTest;
import com.bluesky.simulations.domain.Aircraft;
import com.bluesky.simulations.domain.enumeration.AircraftCategory;
import com.bluesky.simulations.repository.AircraftRepository;
import com.bluesky.simulations.service.dto.AircraftDTO;
import com.bluesky.simulations.service.mapper.AircraftMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link AircraftResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AircraftResourceIT {

    private static final String DEFAULT_TAIL_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_TAIL_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_MODEL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_MODEL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ICAO_TYPE = "AAAA";
    private static final String UPDATED_ICAO_TYPE = "BBBB";

    private static final AircraftCategory DEFAULT_CATEGORY = AircraftCategory.PISTON_SINGLE;
    private static final AircraftCategory UPDATED_CATEGORY = AircraftCategory.TURBOPROP;

    private static final Integer DEFAULT_CRUISE_SPEED_KNOTS = 40;
    private static final Integer UPDATED_CRUISE_SPEED_KNOTS = 41;

    private static final Integer DEFAULT_MAX_RANGE_NM = 100;
    private static final Integer UPDATED_MAX_RANGE_NM = 101;

    private static final Integer DEFAULT_SERVICE_CEILING_FT = 1000;
    private static final Integer UPDATED_SERVICE_CEILING_FT = 1001;

    private static final Double DEFAULT_FUEL_BURN_GPH = 1D;
    private static final Double UPDATED_FUEL_BURN_GPH = 2D;

    private static final Integer DEFAULT_MIN_RUNWAY_LENGTH_FT = 500;
    private static final Integer UPDATED_MIN_RUNWAY_LENGTH_FT = 501;

    private static final String ENTITY_API_URL = "/api/aircraft";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AircraftRepository aircraftRepository;

    @Autowired
    private AircraftMapper aircraftMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAircraftMockMvc;

    private Aircraft aircraft;

    private Aircraft insertedAircraft;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aircraft createEntity() {
        return new Aircraft()
            .tailNumber(DEFAULT_TAIL_NUMBER)
            .modelName(DEFAULT_MODEL_NAME)
            .icaoType(DEFAULT_ICAO_TYPE)
            .category(DEFAULT_CATEGORY)
            .cruiseSpeedKnots(DEFAULT_CRUISE_SPEED_KNOTS)
            .maxRangeNm(DEFAULT_MAX_RANGE_NM)
            .serviceCeilingFt(DEFAULT_SERVICE_CEILING_FT)
            .fuelBurnGph(DEFAULT_FUEL_BURN_GPH)
            .minRunwayLengthFt(DEFAULT_MIN_RUNWAY_LENGTH_FT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Aircraft createUpdatedEntity() {
        return new Aircraft()
            .tailNumber(UPDATED_TAIL_NUMBER)
            .modelName(UPDATED_MODEL_NAME)
            .icaoType(UPDATED_ICAO_TYPE)
            .category(UPDATED_CATEGORY)
            .cruiseSpeedKnots(UPDATED_CRUISE_SPEED_KNOTS)
            .maxRangeNm(UPDATED_MAX_RANGE_NM)
            .serviceCeilingFt(UPDATED_SERVICE_CEILING_FT)
            .fuelBurnGph(UPDATED_FUEL_BURN_GPH)
            .minRunwayLengthFt(UPDATED_MIN_RUNWAY_LENGTH_FT);
    }

    @BeforeEach
    void initTest() {
        aircraft = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAircraft != null) {
            aircraftRepository.delete(insertedAircraft);
            insertedAircraft = null;
        }
    }

    @Test
    @Transactional
    void createAircraft() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);
        var returnedAircraftDTO = om.readValue(
            restAircraftMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AircraftDTO.class
        );

        // Validate the Aircraft in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAircraft = aircraftMapper.toEntity(returnedAircraftDTO);
        assertAircraftUpdatableFieldsEquals(returnedAircraft, getPersistedAircraft(returnedAircraft));

        insertedAircraft = returnedAircraft;
    }

    @Test
    @Transactional
    void createAircraftWithExistingId() throws Exception {
        // Create the Aircraft with an existing ID
        aircraft.setId(1L);
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTailNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setTailNumber(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkModelNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setModelName(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkIcaoTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setIcaoType(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCategoryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setCategory(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCruiseSpeedKnotsIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setCruiseSpeedKnots(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxRangeNmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setMaxRangeNm(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFuelBurnGphIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setFuelBurnGph(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinRunwayLengthFtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        aircraft.setMinRunwayLengthFt(null);

        // Create the Aircraft, which fails.
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        restAircraftMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAircrafts() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        // Get all the aircraftList
        restAircraftMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(aircraft.getId().intValue())))
            .andExpect(jsonPath("$.[*].tailNumber").value(hasItem(DEFAULT_TAIL_NUMBER)))
            .andExpect(jsonPath("$.[*].modelName").value(hasItem(DEFAULT_MODEL_NAME)))
            .andExpect(jsonPath("$.[*].icaoType").value(hasItem(DEFAULT_ICAO_TYPE)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY.toString())))
            .andExpect(jsonPath("$.[*].cruiseSpeedKnots").value(hasItem(DEFAULT_CRUISE_SPEED_KNOTS)))
            .andExpect(jsonPath("$.[*].maxRangeNm").value(hasItem(DEFAULT_MAX_RANGE_NM)))
            .andExpect(jsonPath("$.[*].serviceCeilingFt").value(hasItem(DEFAULT_SERVICE_CEILING_FT)))
            .andExpect(jsonPath("$.[*].fuelBurnGph").value(hasItem(DEFAULT_FUEL_BURN_GPH)))
            .andExpect(jsonPath("$.[*].minRunwayLengthFt").value(hasItem(DEFAULT_MIN_RUNWAY_LENGTH_FT)));
    }

    @Test
    @Transactional
    void getAircraft() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        // Get the aircraft
        restAircraftMockMvc
            .perform(get(ENTITY_API_URL_ID, aircraft.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(aircraft.getId().intValue()))
            .andExpect(jsonPath("$.tailNumber").value(DEFAULT_TAIL_NUMBER))
            .andExpect(jsonPath("$.modelName").value(DEFAULT_MODEL_NAME))
            .andExpect(jsonPath("$.icaoType").value(DEFAULT_ICAO_TYPE))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY.toString()))
            .andExpect(jsonPath("$.cruiseSpeedKnots").value(DEFAULT_CRUISE_SPEED_KNOTS))
            .andExpect(jsonPath("$.maxRangeNm").value(DEFAULT_MAX_RANGE_NM))
            .andExpect(jsonPath("$.serviceCeilingFt").value(DEFAULT_SERVICE_CEILING_FT))
            .andExpect(jsonPath("$.fuelBurnGph").value(DEFAULT_FUEL_BURN_GPH))
            .andExpect(jsonPath("$.minRunwayLengthFt").value(DEFAULT_MIN_RUNWAY_LENGTH_FT));
    }

    @Test
    @Transactional
    void getNonExistingAircraft() throws Exception {
        // Get the aircraft
        restAircraftMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAircraft() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aircraft
        Aircraft updatedAircraft = aircraftRepository.findById(aircraft.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAircraft are not directly saved in db
        em.detach(updatedAircraft);
        updatedAircraft
            .tailNumber(UPDATED_TAIL_NUMBER)
            .modelName(UPDATED_MODEL_NAME)
            .icaoType(UPDATED_ICAO_TYPE)
            .category(UPDATED_CATEGORY)
            .cruiseSpeedKnots(UPDATED_CRUISE_SPEED_KNOTS)
            .maxRangeNm(UPDATED_MAX_RANGE_NM)
            .serviceCeilingFt(UPDATED_SERVICE_CEILING_FT)
            .fuelBurnGph(UPDATED_FUEL_BURN_GPH)
            .minRunwayLengthFt(UPDATED_MIN_RUNWAY_LENGTH_FT);
        AircraftDTO aircraftDTO = aircraftMapper.toDto(updatedAircraft);

        restAircraftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aircraftDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aircraftDTO))
            )
            .andExpect(status().isOk());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAircraftToMatchAllProperties(updatedAircraft);
    }

    @Test
    @Transactional
    void putNonExistingAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, aircraftDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aircraftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(aircraftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAircraftWithPatch() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aircraft using partial update
        Aircraft partialUpdatedAircraft = new Aircraft();
        partialUpdatedAircraft.setId(aircraft.getId());

        partialUpdatedAircraft
            .modelName(UPDATED_MODEL_NAME)
            .serviceCeilingFt(UPDATED_SERVICE_CEILING_FT)
            .fuelBurnGph(UPDATED_FUEL_BURN_GPH)
            .minRunwayLengthFt(UPDATED_MIN_RUNWAY_LENGTH_FT);

        restAircraftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAircraft.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAircraft))
            )
            .andExpect(status().isOk());

        // Validate the Aircraft in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAircraftUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAircraft, aircraft), getPersistedAircraft(aircraft));
    }

    @Test
    @Transactional
    void fullUpdateAircraftWithPatch() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the aircraft using partial update
        Aircraft partialUpdatedAircraft = new Aircraft();
        partialUpdatedAircraft.setId(aircraft.getId());

        partialUpdatedAircraft
            .tailNumber(UPDATED_TAIL_NUMBER)
            .modelName(UPDATED_MODEL_NAME)
            .icaoType(UPDATED_ICAO_TYPE)
            .category(UPDATED_CATEGORY)
            .cruiseSpeedKnots(UPDATED_CRUISE_SPEED_KNOTS)
            .maxRangeNm(UPDATED_MAX_RANGE_NM)
            .serviceCeilingFt(UPDATED_SERVICE_CEILING_FT)
            .fuelBurnGph(UPDATED_FUEL_BURN_GPH)
            .minRunwayLengthFt(UPDATED_MIN_RUNWAY_LENGTH_FT);

        restAircraftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAircraft.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAircraft))
            )
            .andExpect(status().isOk());

        // Validate the Aircraft in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAircraftUpdatableFieldsEquals(partialUpdatedAircraft, getPersistedAircraft(partialUpdatedAircraft));
    }

    @Test
    @Transactional
    void patchNonExistingAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, aircraftDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aircraftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(aircraftDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAircraft() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        aircraft.setId(longCount.incrementAndGet());

        // Create the Aircraft
        AircraftDTO aircraftDTO = aircraftMapper.toDto(aircraft);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAircraftMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(aircraftDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Aircraft in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAircraft() throws Exception {
        // Initialize the database
        insertedAircraft = aircraftRepository.saveAndFlush(aircraft);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the aircraft
        restAircraftMockMvc
            .perform(delete(ENTITY_API_URL_ID, aircraft.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return aircraftRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Aircraft getPersistedAircraft(Aircraft aircraft) {
        return aircraftRepository.findById(aircraft.getId()).orElseThrow();
    }

    protected void assertPersistedAircraftToMatchAllProperties(Aircraft expectedAircraft) {
        assertAircraftAllPropertiesEquals(expectedAircraft, getPersistedAircraft(expectedAircraft));
    }

    protected void assertPersistedAircraftToMatchUpdatableProperties(Aircraft expectedAircraft) {
        assertAircraftAllUpdatablePropertiesEquals(expectedAircraft, getPersistedAircraft(expectedAircraft));
    }
}
