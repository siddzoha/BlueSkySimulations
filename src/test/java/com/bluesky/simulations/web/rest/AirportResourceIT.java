package com.bluesky.simulations.web.rest;

import static com.bluesky.simulations.domain.AirportAsserts.*;
import static com.bluesky.simulations.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bluesky.simulations.IntegrationTest;
import com.bluesky.simulations.domain.Airport;
import com.bluesky.simulations.repository.AirportRepository;
import com.bluesky.simulations.service.dto.AirportDTO;
import com.bluesky.simulations.service.mapper.AirportMapper;
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
 * Integration tests for the {@link AirportResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AirportResourceIT {

    private static final String DEFAULT_ICAO_CODE = "AAAA";
    private static final String UPDATED_ICAO_CODE = "BBBB";

    private static final String DEFAULT_IATA_CODE = "AAA";
    private static final String UPDATED_IATA_CODE = "BBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final Double DEFAULT_LATITUDE = -90D;
    private static final Double UPDATED_LATITUDE = -89D;

    private static final Double DEFAULT_LONGITUDE = -180D;
    private static final Double UPDATED_LONGITUDE = -179D;

    private static final Integer DEFAULT_ELEVATION_FT = -1500;
    private static final Integer UPDATED_ELEVATION_FT = -1499;

    private static final Integer DEFAULT_LONGEST_RUNWAY_FT = 500;
    private static final Integer UPDATED_LONGEST_RUNWAY_FT = 501;

    private static final String ENTITY_API_URL = "/api/airports";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirportMapper airportMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAirportMockMvc;

    private Airport airport;

    private Airport insertedAirport;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Airport createEntity() {
        return new Airport()
            .icaoCode(DEFAULT_ICAO_CODE)
            .iataCode(DEFAULT_IATA_CODE)
            .name(DEFAULT_NAME)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .latitude(DEFAULT_LATITUDE)
            .longitude(DEFAULT_LONGITUDE)
            .elevationFt(DEFAULT_ELEVATION_FT)
            .longestRunwayFt(DEFAULT_LONGEST_RUNWAY_FT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Airport createUpdatedEntity() {
        return new Airport()
            .icaoCode(UPDATED_ICAO_CODE)
            .iataCode(UPDATED_IATA_CODE)
            .name(UPDATED_NAME)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .elevationFt(UPDATED_ELEVATION_FT)
            .longestRunwayFt(UPDATED_LONGEST_RUNWAY_FT);
    }

    @BeforeEach
    void initTest() {
        airport = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAirport != null) {
            airportRepository.delete(insertedAirport);
            insertedAirport = null;
        }
    }

    @Test
    @Transactional
    void createAirport() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);
        var returnedAirportDTO = om.readValue(
            restAirportMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AirportDTO.class
        );

        // Validate the Airport in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAirport = airportMapper.toEntity(returnedAirportDTO);
        assertAirportUpdatableFieldsEquals(returnedAirport, getPersistedAirport(returnedAirport));

        insertedAirport = returnedAirport;
    }

    @Test
    @Transactional
    void createAirportWithExistingId() throws Exception {
        // Create the Airport with an existing ID
        airport.setId(1L);
        AirportDTO airportDTO = airportMapper.toDto(airport);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkIcaoCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setIcaoCode(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setName(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setCity(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setCountry(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLatitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setLatitude(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongitudeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setLongitude(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLongestRunwayFtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        airport.setLongestRunwayFt(null);

        // Create the Airport, which fails.
        AirportDTO airportDTO = airportMapper.toDto(airport);

        restAirportMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAirports() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        // Get all the airportList
        restAirportMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(airport.getId().intValue())))
            .andExpect(jsonPath("$.[*].icaoCode").value(hasItem(DEFAULT_ICAO_CODE)))
            .andExpect(jsonPath("$.[*].iataCode").value(hasItem(DEFAULT_IATA_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].latitude").value(hasItem(DEFAULT_LATITUDE)))
            .andExpect(jsonPath("$.[*].longitude").value(hasItem(DEFAULT_LONGITUDE)))
            .andExpect(jsonPath("$.[*].elevationFt").value(hasItem(DEFAULT_ELEVATION_FT)))
            .andExpect(jsonPath("$.[*].longestRunwayFt").value(hasItem(DEFAULT_LONGEST_RUNWAY_FT)));
    }

    @Test
    @Transactional
    void getAirport() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        // Get the airport
        restAirportMockMvc
            .perform(get(ENTITY_API_URL_ID, airport.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(airport.getId().intValue()))
            .andExpect(jsonPath("$.icaoCode").value(DEFAULT_ICAO_CODE))
            .andExpect(jsonPath("$.iataCode").value(DEFAULT_IATA_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY))
            .andExpect(jsonPath("$.latitude").value(DEFAULT_LATITUDE))
            .andExpect(jsonPath("$.longitude").value(DEFAULT_LONGITUDE))
            .andExpect(jsonPath("$.elevationFt").value(DEFAULT_ELEVATION_FT))
            .andExpect(jsonPath("$.longestRunwayFt").value(DEFAULT_LONGEST_RUNWAY_FT));
    }

    @Test
    @Transactional
    void getNonExistingAirport() throws Exception {
        // Get the airport
        restAirportMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAirport() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the airport
        Airport updatedAirport = airportRepository.findById(airport.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAirport are not directly saved in db
        em.detach(updatedAirport);
        updatedAirport
            .icaoCode(UPDATED_ICAO_CODE)
            .iataCode(UPDATED_IATA_CODE)
            .name(UPDATED_NAME)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .elevationFt(UPDATED_ELEVATION_FT)
            .longestRunwayFt(UPDATED_LONGEST_RUNWAY_FT);
        AirportDTO airportDTO = airportMapper.toDto(updatedAirport);

        restAirportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, airportDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO))
            )
            .andExpect(status().isOk());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAirportToMatchAllProperties(updatedAirport);
    }

    @Test
    @Transactional
    void putNonExistingAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, airportDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(airportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAirportWithPatch() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the airport using partial update
        Airport partialUpdatedAirport = new Airport();
        partialUpdatedAirport.setId(airport.getId());

        partialUpdatedAirport
            .icaoCode(UPDATED_ICAO_CODE)
            .iataCode(UPDATED_IATA_CODE)
            .name(UPDATED_NAME)
            .country(UPDATED_COUNTRY)
            .latitude(UPDATED_LATITUDE)
            .elevationFt(UPDATED_ELEVATION_FT)
            .longestRunwayFt(UPDATED_LONGEST_RUNWAY_FT);

        restAirportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAirport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAirport))
            )
            .andExpect(status().isOk());

        // Validate the Airport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAirportUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedAirport, airport), getPersistedAirport(airport));
    }

    @Test
    @Transactional
    void fullUpdateAirportWithPatch() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the airport using partial update
        Airport partialUpdatedAirport = new Airport();
        partialUpdatedAirport.setId(airport.getId());

        partialUpdatedAirport
            .icaoCode(UPDATED_ICAO_CODE)
            .iataCode(UPDATED_IATA_CODE)
            .name(UPDATED_NAME)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .latitude(UPDATED_LATITUDE)
            .longitude(UPDATED_LONGITUDE)
            .elevationFt(UPDATED_ELEVATION_FT)
            .longestRunwayFt(UPDATED_LONGEST_RUNWAY_FT);

        restAirportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAirport.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAirport))
            )
            .andExpect(status().isOk());

        // Validate the Airport in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAirportUpdatableFieldsEquals(partialUpdatedAirport, getPersistedAirport(partialUpdatedAirport));
    }

    @Test
    @Transactional
    void patchNonExistingAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, airportDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(airportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(airportDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAirport() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        airport.setId(longCount.incrementAndGet());

        // Create the Airport
        AirportDTO airportDTO = airportMapper.toDto(airport);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAirportMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(airportDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Airport in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAirport() throws Exception {
        // Initialize the database
        insertedAirport = airportRepository.saveAndFlush(airport);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the airport
        restAirportMockMvc
            .perform(delete(ENTITY_API_URL_ID, airport.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return airportRepository.count();
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

    protected Airport getPersistedAirport(Airport airport) {
        return airportRepository.findById(airport.getId()).orElseThrow();
    }

    protected void assertPersistedAirportToMatchAllProperties(Airport expectedAirport) {
        assertAirportAllPropertiesEquals(expectedAirport, getPersistedAirport(expectedAirport));
    }

    protected void assertPersistedAirportToMatchUpdatableProperties(Airport expectedAirport) {
        assertAirportAllUpdatablePropertiesEquals(expectedAirport, getPersistedAirport(expectedAirport));
    }
}
