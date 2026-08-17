package com.bluesky.simulations.web.rest;

import static com.bluesky.simulations.domain.AchievementAsserts.*;
import static com.bluesky.simulations.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.bluesky.simulations.IntegrationTest;
import com.bluesky.simulations.domain.Achievement;
import com.bluesky.simulations.repository.AchievementRepository;
import com.bluesky.simulations.service.dto.AchievementDTO;
import com.bluesky.simulations.service.mapper.AchievementMapper;
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
 * Integration tests for the {@link AchievementResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class AchievementResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_BADGE_ICON = "AAAAAAAAAA";
    private static final String UPDATED_BADGE_ICON = "BBBBBBBBBB";

    private static final Integer DEFAULT_XP_REWARD = 0;
    private static final Integer UPDATED_XP_REWARD = 1;

    private static final String ENTITY_API_URL = "/api/achievements";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + 2L * Integer.MAX_VALUE);

    @Autowired
    private ObjectMapper om;

    @Autowired
    private AchievementRepository achievementRepository;

    @Autowired
    private AchievementMapper achievementMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restAchievementMockMvc;

    private Achievement achievement;

    private Achievement insertedAchievement;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Achievement createEntity() {
        return new Achievement()
            .code(DEFAULT_CODE)
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .badgeIcon(DEFAULT_BADGE_ICON)
            .xpReward(DEFAULT_XP_REWARD);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Achievement createUpdatedEntity() {
        return new Achievement()
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .badgeIcon(UPDATED_BADGE_ICON)
            .xpReward(UPDATED_XP_REWARD);
    }

    @BeforeEach
    void initTest() {
        achievement = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedAchievement != null) {
            achievementRepository.delete(insertedAchievement);
            insertedAchievement = null;
        }
    }

    @Test
    @Transactional
    void createAchievement() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);
        var returnedAchievementDTO = om.readValue(
            restAchievementMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            AchievementDTO.class
        );

        // Validate the Achievement in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedAchievement = achievementMapper.toEntity(returnedAchievementDTO);
        assertAchievementUpdatableFieldsEquals(returnedAchievement, getPersistedAchievement(returnedAchievement));

        insertedAchievement = returnedAchievement;
    }

    @Test
    @Transactional
    void createAchievementWithExistingId() throws Exception {
        // Create the Achievement with an existing ID
        achievement.setId(1L);
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restAchievementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        achievement.setCode(null);

        // Create the Achievement, which fails.
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        restAchievementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        achievement.setTitle(null);

        // Create the Achievement, which fails.
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        restAchievementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        achievement.setDescription(null);

        // Create the Achievement, which fails.
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        restAchievementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkXpRewardIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        achievement.setXpReward(null);

        // Create the Achievement, which fails.
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        restAchievementMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllAchievements() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        // Get all the achievementList
        restAchievementMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(achievement.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].badgeIcon").value(hasItem(DEFAULT_BADGE_ICON)))
            .andExpect(jsonPath("$.[*].xpReward").value(hasItem(DEFAULT_XP_REWARD)));
    }

    @Test
    @Transactional
    void getAchievement() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        // Get the achievement
        restAchievementMockMvc
            .perform(get(ENTITY_API_URL_ID, achievement.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(achievement.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.badgeIcon").value(DEFAULT_BADGE_ICON))
            .andExpect(jsonPath("$.xpReward").value(DEFAULT_XP_REWARD));
    }

    @Test
    @Transactional
    void getNonExistingAchievement() throws Exception {
        // Get the achievement
        restAchievementMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingAchievement() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the achievement
        Achievement updatedAchievement = achievementRepository.findById(achievement.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedAchievement are not directly saved in db
        em.detach(updatedAchievement);
        updatedAchievement
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .badgeIcon(UPDATED_BADGE_ICON)
            .xpReward(UPDATED_XP_REWARD);
        AchievementDTO achievementDTO = achievementMapper.toDto(updatedAchievement);

        restAchievementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, achievementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(achievementDTO))
            )
            .andExpect(status().isOk());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedAchievementToMatchAllProperties(updatedAchievement);
    }

    @Test
    @Transactional
    void putNonExistingAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, achievementDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(achievementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(achievementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateAchievementWithPatch() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the achievement using partial update
        Achievement partialUpdatedAchievement = new Achievement();
        partialUpdatedAchievement.setId(achievement.getId());

        partialUpdatedAchievement.badgeIcon(UPDATED_BADGE_ICON);

        restAchievementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAchievement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAchievement))
            )
            .andExpect(status().isOk());

        // Validate the Achievement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAchievementUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedAchievement, achievement),
            getPersistedAchievement(achievement)
        );
    }

    @Test
    @Transactional
    void fullUpdateAchievementWithPatch() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the achievement using partial update
        Achievement partialUpdatedAchievement = new Achievement();
        partialUpdatedAchievement.setId(achievement.getId());

        partialUpdatedAchievement
            .code(UPDATED_CODE)
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .badgeIcon(UPDATED_BADGE_ICON)
            .xpReward(UPDATED_XP_REWARD);

        restAchievementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedAchievement.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedAchievement))
            )
            .andExpect(status().isOk());

        // Validate the Achievement in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertAchievementUpdatableFieldsEquals(partialUpdatedAchievement, getPersistedAchievement(partialUpdatedAchievement));
    }

    @Test
    @Transactional
    void patchNonExistingAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, achievementDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(achievementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(achievementDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamAchievement() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        achievement.setId(longCount.incrementAndGet());

        // Create the Achievement
        AchievementDTO achievementDTO = achievementMapper.toDto(achievement);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restAchievementMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(achievementDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Achievement in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteAchievement() throws Exception {
        // Initialize the database
        insertedAchievement = achievementRepository.saveAndFlush(achievement);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the achievement
        restAchievementMockMvc
            .perform(delete(ENTITY_API_URL_ID, achievement.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return achievementRepository.count();
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

    protected Achievement getPersistedAchievement(Achievement achievement) {
        return achievementRepository.findById(achievement.getId()).orElseThrow();
    }

    protected void assertPersistedAchievementToMatchAllProperties(Achievement expectedAchievement) {
        assertAchievementAllPropertiesEquals(expectedAchievement, getPersistedAchievement(expectedAchievement));
    }

    protected void assertPersistedAchievementToMatchUpdatableProperties(Achievement expectedAchievement) {
        assertAchievementAllUpdatablePropertiesEquals(expectedAchievement, getPersistedAchievement(expectedAchievement));
    }
}
