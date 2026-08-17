package com.bluesky.simulations.service.impl;

import com.bluesky.simulations.domain.Achievement;
import com.bluesky.simulations.repository.AchievementRepository;
import com.bluesky.simulations.service.AchievementService;
import com.bluesky.simulations.service.dto.AchievementDTO;
import com.bluesky.simulations.service.mapper.AchievementMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bluesky.simulations.domain.Achievement}.
 */
@Service
@Transactional
public class AchievementServiceImpl implements AchievementService {

    private static final Logger LOG = LoggerFactory.getLogger(AchievementServiceImpl.class);

    private final AchievementRepository achievementRepository;

    private final AchievementMapper achievementMapper;

    public AchievementServiceImpl(AchievementRepository achievementRepository, AchievementMapper achievementMapper) {
        this.achievementRepository = achievementRepository;
        this.achievementMapper = achievementMapper;
    }

    @Override
    public AchievementDTO save(AchievementDTO achievementDTO) {
        LOG.debug("Request to save Achievement : {}", achievementDTO);
        Achievement achievement = achievementMapper.toEntity(achievementDTO);
        achievement = achievementRepository.save(achievement);
        return achievementMapper.toDto(achievement);
    }

    @Override
    public AchievementDTO update(AchievementDTO achievementDTO) {
        LOG.debug("Request to update Achievement : {}", achievementDTO);
        Achievement achievement = achievementMapper.toEntity(achievementDTO);
        achievement = achievementRepository.save(achievement);
        return achievementMapper.toDto(achievement);
    }

    @Override
    public Optional<AchievementDTO> partialUpdate(AchievementDTO achievementDTO) {
        LOG.debug("Request to partially update Achievement : {}", achievementDTO);

        return achievementRepository
            .findById(achievementDTO.getId())
            .map(existingAchievement -> {
                achievementMapper.partialUpdate(existingAchievement, achievementDTO);

                return existingAchievement;
            })
            .map(achievementRepository::save)
            .map(achievementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AchievementDTO> findAll() {
        LOG.debug("Request to get all Achievements");
        return achievementRepository.findAll().stream().map(achievementMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AchievementDTO> findOne(Long id) {
        LOG.debug("Request to get Achievement : {}", id);
        return achievementRepository.findById(id).map(achievementMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Achievement : {}", id);
        achievementRepository.deleteById(id);
    }
}
