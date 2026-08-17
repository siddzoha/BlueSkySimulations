package com.bluesky.simulations.web.rest;

import static com.bluesky.simulations.domain.FlightLogAsserts.*;
import static com.bluesky.simulations.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bluesky.simulations.IntegrationTest;
import com.bluesky.simulations.domain.FlightLog;
import com.bluesky.simulations.domain.enumeration.FlightRules;
import com.bluesky.simulations.repository.FlightLogRepository;
import com.bluesky.simulations.service.FlightLogService;
import com.bluesky.simulations.service.dto.FlightLogDTO;
import com.bluesky.simulations.service.mapper.FlightLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link FlightLogResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class FlightLogResourceIT {

    private static final Instant DEFAULT_DEPARTURE_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPARTURE_TIME = Instant.ofEpochMilli(1702869675843L);

    private static final Instant DEFAULT_ARRIVAL_TIME = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_ARRIVAL_TIME = Instant.ofEpochMilli(1702869675843L);

    private static final Double DEFAULT_DURATION_HOURS = 0D;
    private static final Double UPDATED_DURATION_HOURS = 1D;

    private static final Integer DEFAULT_DISTANCE_NM = 1;
    private static final Integer UPDATED_DISTANCE_NM = 2;

    private static final Integer DEFAULT_CRUISE_ALTITUDE_FT = 1000;
    private static final Integer UPDATED_CRUISE_ALTITUDE_FT = 1001;

    private static final Double DEFAULT_FUEL_USED_GALLONS = 0D;
    private static final Double UPDATED_FUEL_USED_GALLONS = 1D;

    private static final Integer DEFAULT_XP_EARNED = 0;
    private static final Integer UPDATED_XP_EARNED = 1;

    private static final FlightRules DEFAULT_FLIGHT_RULES = FlightRules.VFR;
    private static final FlightRules UPDATED_FLIGHT_RULES = FlightRules.IFR;

    private static final String DEFAULT_REMARKS = "AAAAAAAAAA";
    private static final String UPDATED_REMARKS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/flight-logs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FlightLogRepository flightLogRepository;

    @Mock
    private FlightLogRepository flightLogRepositoryMock;

    @Autowired
    private FlightLogMapper flightLogMapper;

    @Mock
    private FlightLogService flightLogServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFlightLogMockMvc;

    private FlightLog flightLog;

    private FlightLog insertedFlightLog;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlightLog createEntity() {
        return new FlightLog()
            .departureTime(DEFAULT_DEPARTURE_TIME)
            .arrivalTime(DEFAULT_ARRIVAL_TIME)
            .durationHours(DEFAULT_DURATION_HOURS)
            .distanceNm(DEFAULT_DISTANCE_NM)
            .cruiseAltitudeFt(DEFAULT_CRUISE_ALTITUDE_FT)
            .fuelUsedGallons(DEFAULT_FUEL_USED_GALLONS)
            .xpEarned(DEFAULT_XP_EARNED)
            .flightRules(DEFAULT_FLIGHT_RULES)
            .remarks(DEFAULT_REMARKS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static FlightLog createUpdatedEntity() {
        return new FlightLog()
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .durationHours(UPDATED_DURATION_HOURS)
            .distanceNm(UPDATED_DISTANCE_NM)
            .cruiseAltitudeFt(UPDATED_CRUISE_ALTITUDE_FT)
            .fuelUsedGallons(UPDATED_FUEL_USED_GALLONS)
            .xpEarned(UPDATED_XP_EARNED)
            .flightRules(UPDATED_FLIGHT_RULES)
            .remarks(UPDATED_REMARKS);
    }

    @BeforeEach
    void initTest() {
        flightLog = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedFlightLog != null) {
            flightLogRepository.delete(insertedFlightLog);
            insertedFlightLog = null;
        }
    }

    @Test
    @Transactional
    void createFlightLog() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);
        var returnedFlightLogDTO = om.readValue(
            restFlightLogMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            FlightLogDTO.class
        );

        // Validate the FlightLog in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedFlightLog = flightLogMapper.toEntity(returnedFlightLogDTO);
        assertFlightLogUpdatableFieldsEquals(returnedFlightLog, getPersistedFlightLog(returnedFlightLog));

        insertedFlightLog = returnedFlightLog;
    }

    @Test
    @Transactional
    void createFlightLogWithExistingId() throws Exception {
        // Create the FlightLog with an existing ID
        flightLog.setId(1L);
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDepartureTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flightLog.setDepartureTime(null);

        // Create the FlightLog, which fails.
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkArrivalTimeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flightLog.setArrivalTime(null);

        // Create the FlightLog, which fails.
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDurationHoursIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flightLog.setDurationHours(null);

        // Create the FlightLog, which fails.
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDistanceNmIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flightLog.setDistanceNm(null);

        // Create the FlightLog, which fails.
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFlightRulesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        flightLog.setFlightRules(null);

        // Create the FlightLog, which fails.
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        restFlightLogMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFlightLogs() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        // Get all the flightLogList
        restFlightLogMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(flightLog.getId().intValue())))
            .andExpect(jsonPath("$.[*].departureTime").value(hasItem(DEFAULT_DEPARTURE_TIME.toString())))
            .andExpect(jsonPath("$.[*].arrivalTime").value(hasItem(DEFAULT_ARRIVAL_TIME.toString())))
            .andExpect(jsonPath("$.[*].durationHours").value(hasItem(DEFAULT_DURATION_HOURS)))
            .andExpect(jsonPath("$.[*].distanceNm").value(hasItem(DEFAULT_DISTANCE_NM)))
            .andExpect(jsonPath("$.[*].cruiseAltitudeFt").value(hasItem(DEFAULT_CRUISE_ALTITUDE_FT)))
            .andExpect(jsonPath("$.[*].fuelUsedGallons").value(hasItem(DEFAULT_FUEL_USED_GALLONS)))
            .andExpect(jsonPath("$.[*].xpEarned").value(hasItem(DEFAULT_XP_EARNED)))
            .andExpect(jsonPath("$.[*].flightRules").value(hasItem(DEFAULT_FLIGHT_RULES.toString())))
            .andExpect(jsonPath("$.[*].remarks").value(hasItem(DEFAULT_REMARKS)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlightLogsWithEagerRelationshipsIsEnabled() throws Exception {
        when(flightLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlightLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(flightLogServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllFlightLogsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(flightLogServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restFlightLogMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(flightLogRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getFlightLog() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        // Get the flightLog
        restFlightLogMockMvc
            .perform(get(ENTITY_API_URL_ID, flightLog.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(flightLog.getId().intValue()))
            .andExpect(jsonPath("$.departureTime").value(DEFAULT_DEPARTURE_TIME.toString()))
            .andExpect(jsonPath("$.arrivalTime").value(DEFAULT_ARRIVAL_TIME.toString()))
            .andExpect(jsonPath("$.durationHours").value(DEFAULT_DURATION_HOURS))
            .andExpect(jsonPath("$.distanceNm").value(DEFAULT_DISTANCE_NM))
            .andExpect(jsonPath("$.cruiseAltitudeFt").value(DEFAULT_CRUISE_ALTITUDE_FT))
            .andExpect(jsonPath("$.fuelUsedGallons").value(DEFAULT_FUEL_USED_GALLONS))
            .andExpect(jsonPath("$.xpEarned").value(DEFAULT_XP_EARNED))
            .andExpect(jsonPath("$.flightRules").value(DEFAULT_FLIGHT_RULES.toString()))
            .andExpect(jsonPath("$.remarks").value(DEFAULT_REMARKS));
    }

    @Test
    @Transactional
    void getNonExistingFlightLog() throws Exception {
        // Get the flightLog
        restFlightLogMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFlightLog() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flightLog
        FlightLog updatedFlightLog = flightLogRepository.findById(flightLog.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFlightLog are not directly saved in db
        em.detach(updatedFlightLog);
        updatedFlightLog
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .durationHours(UPDATED_DURATION_HOURS)
            .distanceNm(UPDATED_DISTANCE_NM)
            .cruiseAltitudeFt(UPDATED_CRUISE_ALTITUDE_FT)
            .fuelUsedGallons(UPDATED_FUEL_USED_GALLONS)
            .xpEarned(UPDATED_XP_EARNED)
            .flightRules(UPDATED_FLIGHT_RULES)
            .remarks(UPDATED_REMARKS);
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(updatedFlightLog);

        restFlightLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flightLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flightLogDTO))
            )
            .andExpect(status().isOk());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFlightLogToMatchAllProperties(updatedFlightLog);
    }

    @Test
    @Transactional
    void putNonExistingFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, flightLogDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flightLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(flightLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFlightLogWithPatch() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flightLog using partial update
        FlightLog partialUpdatedFlightLog = new FlightLog();
        partialUpdatedFlightLog.setId(flightLog.getId());

        partialUpdatedFlightLog
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .durationHours(UPDATED_DURATION_HOURS)
            .distanceNm(UPDATED_DISTANCE_NM)
            .fuelUsedGallons(UPDATED_FUEL_USED_GALLONS)
            .remarks(UPDATED_REMARKS);

        restFlightLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlightLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlightLog))
            )
            .andExpect(status().isOk());

        // Validate the FlightLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlightLogUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedFlightLog, flightLog),
            getPersistedFlightLog(flightLog)
        );
    }

    @Test
    @Transactional
    void fullUpdateFlightLogWithPatch() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the flightLog using partial update
        FlightLog partialUpdatedFlightLog = new FlightLog();
        partialUpdatedFlightLog.setId(flightLog.getId());

        partialUpdatedFlightLog
            .departureTime(UPDATED_DEPARTURE_TIME)
            .arrivalTime(UPDATED_ARRIVAL_TIME)
            .durationHours(UPDATED_DURATION_HOURS)
            .distanceNm(UPDATED_DISTANCE_NM)
            .cruiseAltitudeFt(UPDATED_CRUISE_ALTITUDE_FT)
            .fuelUsedGallons(UPDATED_FUEL_USED_GALLONS)
            .xpEarned(UPDATED_XP_EARNED)
            .flightRules(UPDATED_FLIGHT_RULES)
            .remarks(UPDATED_REMARKS);

        restFlightLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFlightLog.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFlightLog))
            )
            .andExpect(status().isOk());

        // Validate the FlightLog in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFlightLogUpdatableFieldsEquals(partialUpdatedFlightLog, getPersistedFlightLog(partialUpdatedFlightLog));
    }

    @Test
    @Transactional
    void patchNonExistingFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, flightLogDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flightLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(flightLogDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFlightLog() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        flightLog.setId(longCount.incrementAndGet());

        // Create the FlightLog
        FlightLogDTO flightLogDTO = flightLogMapper.toDto(flightLog);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFlightLogMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(flightLogDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the FlightLog in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFlightLog() throws Exception {
        // Initialize the database
        insertedFlightLog = flightLogRepository.saveAndFlush(flightLog);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the flightLog
        restFlightLogMockMvc
            .perform(delete(ENTITY_API_URL_ID, flightLog.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return flightLogRepository.count();
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

    protected FlightLog getPersistedFlightLog(FlightLog flightLog) {
        return flightLogRepository.findById(flightLog.getId()).orElseThrow();
    }

    protected void assertPersistedFlightLogToMatchAllProperties(FlightLog expectedFlightLog) {
        assertFlightLogAllPropertiesEquals(expectedFlightLog, getPersistedFlightLog(expectedFlightLog));
    }

    protected void assertPersistedFlightLogToMatchUpdatableProperties(FlightLog expectedFlightLog) {
        assertFlightLogAllUpdatablePropertiesEquals(expectedFlightLog, getPersistedFlightLog(expectedFlightLog));
    }
}
