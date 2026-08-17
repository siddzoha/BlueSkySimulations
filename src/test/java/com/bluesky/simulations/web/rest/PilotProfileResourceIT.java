package com.bluesky.simulations.web.rest;

import static com.bluesky.simulations.domain.PilotProfileAsserts.*;
import static com.bluesky.simulations.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bluesky.simulations.IntegrationTest;
import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.domain.enumeration.RankTier;
import com.bluesky.simulations.repository.PilotProfileRepository;
import com.bluesky.simulations.repository.UserRepository;
import com.bluesky.simulations.service.PilotProfileService;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import com.bluesky.simulations.service.mapper.PilotProfileMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PilotProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PilotProfileResourceIT {

    private static final Integer DEFAULT_TOTAL_XP = 0;
    private static final Integer UPDATED_TOTAL_XP = 1;

    private static final RankTier DEFAULT_RANK_TIER = RankTier.STUDENT;
    private static final RankTier UPDATED_RANK_TIER = RankTier.FIRST_OFFICER;

    private static final Double DEFAULT_TOTAL_FLIGHT_HOURS = 0D;
    private static final Double UPDATED_TOTAL_FLIGHT_HOURS = 1D;

    private static final Double DEFAULT_TOTAL_NIGHT_FLIGHT_HOURS = 0D;
    private static final Double UPDATED_TOTAL_NIGHT_FLIGHT_HOURS = 1D;

    private static final Double DEFAULT_TOTAL_IFR_FLIGHT_HOURS = 0D;
    private static final Double UPDATED_TOTAL_IFR_FLIGHT_HOURS = 1D;

    private static final Integer DEFAULT_FLIGHTS_COMPLETED = 0;
    private static final Integer UPDATED_FLIGHTS_COMPLETED = 1;

    private static final String ENTITY_API_URL = "/api/pilot-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PilotProfileRepository pilotProfileRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PilotProfileRepository pilotProfileRepositoryMock;

    @Autowired
    private PilotProfileMapper pilotProfileMapper;

    @Mock
    private PilotProfileService pilotProfileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPilotProfileMockMvc;

    private PilotProfile pilotProfile;

    private PilotProfile insertedPilotProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PilotProfile createEntity() {
        return new PilotProfile()
            .totalXp(DEFAULT_TOTAL_XP)
            .rankTier(DEFAULT_RANK_TIER)
            .totalFlightHours(DEFAULT_TOTAL_FLIGHT_HOURS)
            .totalNightFlightHours(DEFAULT_TOTAL_NIGHT_FLIGHT_HOURS)
            .totalIfrFlightHours(DEFAULT_TOTAL_IFR_FLIGHT_HOURS)
            .flightsCompleted(DEFAULT_FLIGHTS_COMPLETED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PilotProfile createUpdatedEntity() {
        return new PilotProfile()
            .totalXp(UPDATED_TOTAL_XP)
            .rankTier(UPDATED_RANK_TIER)
            .totalFlightHours(UPDATED_TOTAL_FLIGHT_HOURS)
            .totalNightFlightHours(UPDATED_TOTAL_NIGHT_FLIGHT_HOURS)
            .totalIfrFlightHours(UPDATED_TOTAL_IFR_FLIGHT_HOURS)
            .flightsCompleted(UPDATED_FLIGHTS_COMPLETED);
    }

    @BeforeEach
    void initTest() {
        pilotProfile = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPilotProfile != null) {
            pilotProfileRepository.delete(insertedPilotProfile);
            insertedPilotProfile = null;
        }
    }

    @Test
    @Transactional
    void createPilotProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);
        var returnedPilotProfileDTO = om.readValue(
            restPilotProfileMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PilotProfileDTO.class
        );

        // Validate the PilotProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPilotProfile = pilotProfileMapper.toEntity(returnedPilotProfileDTO);
        assertPilotProfileUpdatableFieldsEquals(returnedPilotProfile, getPersistedPilotProfile(returnedPilotProfile));

        insertedPilotProfile = returnedPilotProfile;
    }

    @Test
    @Transactional
    void createPilotProfileWithExistingId() throws Exception {
        // Create the PilotProfile with an existing ID
        pilotProfile.setId(1L);
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTotalXpIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setTotalXp(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRankTierIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setRankTier(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalFlightHoursIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setTotalFlightHours(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalNightFlightHoursIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setTotalNightFlightHours(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTotalIfrFlightHoursIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setTotalIfrFlightHours(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkFlightsCompletedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pilotProfile.setFlightsCompleted(null);

        // Create the PilotProfile, which fails.
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        restPilotProfileMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPilotProfiles() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        // Get all the pilotProfileList
        restPilotProfileMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pilotProfile.getId().intValue())))
            .andExpect(jsonPath("$.[*].totalXp").value(hasItem(DEFAULT_TOTAL_XP)))
            .andExpect(jsonPath("$.[*].rankTier").value(hasItem(DEFAULT_RANK_TIER.toString())))
            .andExpect(jsonPath("$.[*].totalFlightHours").value(hasItem(DEFAULT_TOTAL_FLIGHT_HOURS)))
            .andExpect(jsonPath("$.[*].totalNightFlightHours").value(hasItem(DEFAULT_TOTAL_NIGHT_FLIGHT_HOURS)))
            .andExpect(jsonPath("$.[*].totalIfrFlightHours").value(hasItem(DEFAULT_TOTAL_IFR_FLIGHT_HOURS)))
            .andExpect(jsonPath("$.[*].flightsCompleted").value(hasItem(DEFAULT_FLIGHTS_COMPLETED)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPilotProfilesWithEagerRelationshipsIsEnabled() throws Exception {
        when(pilotProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPilotProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pilotProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPilotProfilesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pilotProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPilotProfileMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pilotProfileRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPilotProfile() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        // Get the pilotProfile
        restPilotProfileMockMvc
            .perform(get(ENTITY_API_URL_ID, pilotProfile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pilotProfile.getId().intValue()))
            .andExpect(jsonPath("$.totalXp").value(DEFAULT_TOTAL_XP))
            .andExpect(jsonPath("$.rankTier").value(DEFAULT_RANK_TIER.toString()))
            .andExpect(jsonPath("$.totalFlightHours").value(DEFAULT_TOTAL_FLIGHT_HOURS))
            .andExpect(jsonPath("$.totalNightFlightHours").value(DEFAULT_TOTAL_NIGHT_FLIGHT_HOURS))
            .andExpect(jsonPath("$.totalIfrFlightHours").value(DEFAULT_TOTAL_IFR_FLIGHT_HOURS))
            .andExpect(jsonPath("$.flightsCompleted").value(DEFAULT_FLIGHTS_COMPLETED));
    }

    @Test
    @Transactional
    void getNonExistingPilotProfile() throws Exception {
        // Get the pilotProfile
        restPilotProfileMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPilotProfile() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pilotProfile
        PilotProfile updatedPilotProfile = pilotProfileRepository.findById(pilotProfile.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPilotProfile are not directly saved in db
        em.detach(updatedPilotProfile);
        updatedPilotProfile
            .totalXp(UPDATED_TOTAL_XP)
            .rankTier(UPDATED_RANK_TIER)
            .totalFlightHours(UPDATED_TOTAL_FLIGHT_HOURS)
            .totalNightFlightHours(UPDATED_TOTAL_NIGHT_FLIGHT_HOURS)
            .totalIfrFlightHours(UPDATED_TOTAL_IFR_FLIGHT_HOURS)
            .flightsCompleted(UPDATED_FLIGHTS_COMPLETED);
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(updatedPilotProfile);

        restPilotProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pilotProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pilotProfileDTO))
            )
            .andExpect(status().isOk());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPilotProfileToMatchAllProperties(updatedPilotProfile);
    }

    @Test
    @Transactional
    void putNonExistingPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pilotProfileDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pilotProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pilotProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePilotProfileWithPatch() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pilotProfile using partial update
        PilotProfile partialUpdatedPilotProfile = new PilotProfile();
        partialUpdatedPilotProfile.setId(pilotProfile.getId());

        partialUpdatedPilotProfile
            .totalXp(UPDATED_TOTAL_XP)
            .rankTier(UPDATED_RANK_TIER)
            .totalNightFlightHours(UPDATED_TOTAL_NIGHT_FLIGHT_HOURS)
            .totalIfrFlightHours(UPDATED_TOTAL_IFR_FLIGHT_HOURS)
            .flightsCompleted(UPDATED_FLIGHTS_COMPLETED);

        restPilotProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPilotProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPilotProfile))
            )
            .andExpect(status().isOk());

        // Validate the PilotProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPilotProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedPilotProfile, pilotProfile),
            getPersistedPilotProfile(pilotProfile)
        );
    }

    @Test
    @Transactional
    void fullUpdatePilotProfileWithPatch() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pilotProfile using partial update
        PilotProfile partialUpdatedPilotProfile = new PilotProfile();
        partialUpdatedPilotProfile.setId(pilotProfile.getId());

        partialUpdatedPilotProfile
            .totalXp(UPDATED_TOTAL_XP)
            .rankTier(UPDATED_RANK_TIER)
            .totalFlightHours(UPDATED_TOTAL_FLIGHT_HOURS)
            .totalNightFlightHours(UPDATED_TOTAL_NIGHT_FLIGHT_HOURS)
            .totalIfrFlightHours(UPDATED_TOTAL_IFR_FLIGHT_HOURS)
            .flightsCompleted(UPDATED_FLIGHTS_COMPLETED);

        restPilotProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPilotProfile.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPilotProfile))
            )
            .andExpect(status().isOk());

        // Validate the PilotProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPilotProfileUpdatableFieldsEquals(partialUpdatedPilotProfile, getPersistedPilotProfile(partialUpdatedPilotProfile));
    }

    @Test
    @Transactional
    void patchNonExistingPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pilotProfileDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pilotProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pilotProfileDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPilotProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pilotProfile.setId(longCount.incrementAndGet());

        // Create the PilotProfile
        PilotProfileDTO pilotProfileDTO = pilotProfileMapper.toDto(pilotProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPilotProfileMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pilotProfileDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PilotProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePilotProfile() throws Exception {
        // Initialize the database
        insertedPilotProfile = pilotProfileRepository.saveAndFlush(pilotProfile);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pilotProfile
        restPilotProfileMockMvc
            .perform(delete(ENTITY_API_URL_ID, pilotProfile.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pilotProfileRepository.count();
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

    protected PilotProfile getPersistedPilotProfile(PilotProfile pilotProfile) {
        return pilotProfileRepository.findById(pilotProfile.getId()).orElseThrow();
    }

    protected void assertPersistedPilotProfileToMatchAllProperties(PilotProfile expectedPilotProfile) {
        assertPilotProfileAllPropertiesEquals(expectedPilotProfile, getPersistedPilotProfile(expectedPilotProfile));
    }

    protected void assertPersistedPilotProfileToMatchUpdatableProperties(PilotProfile expectedPilotProfile) {
        assertPilotProfileAllUpdatablePropertiesEquals(expectedPilotProfile, getPersistedPilotProfile(expectedPilotProfile));
    }
}
