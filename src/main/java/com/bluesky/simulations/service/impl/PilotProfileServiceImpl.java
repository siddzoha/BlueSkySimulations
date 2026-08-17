package com.bluesky.simulations.service.impl;

import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.repository.PilotProfileRepository;
import com.bluesky.simulations.service.PilotProfileService;
import com.bluesky.simulations.service.dto.PilotProfileDTO;
import com.bluesky.simulations.service.mapper.PilotProfileMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bluesky.simulations.domain.PilotProfile}.
 */
@Service
@Transactional
public class PilotProfileServiceImpl implements PilotProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(PilotProfileServiceImpl.class);

    private final PilotProfileRepository pilotProfileRepository;

    private final PilotProfileMapper pilotProfileMapper;

    public PilotProfileServiceImpl(PilotProfileRepository pilotProfileRepository, PilotProfileMapper pilotProfileMapper) {
        this.pilotProfileRepository = pilotProfileRepository;
        this.pilotProfileMapper = pilotProfileMapper;
    }

    @Override
    public PilotProfileDTO save(PilotProfileDTO pilotProfileDTO) {
        LOG.debug("Request to save PilotProfile : {}", pilotProfileDTO);
        PilotProfile pilotProfile = pilotProfileMapper.toEntity(pilotProfileDTO);
        pilotProfile = pilotProfileRepository.save(pilotProfile);
        return pilotProfileMapper.toDto(pilotProfile);
    }

    @Override
    public PilotProfileDTO update(PilotProfileDTO pilotProfileDTO) {
        LOG.debug("Request to update PilotProfile : {}", pilotProfileDTO);
        PilotProfile pilotProfile = pilotProfileMapper.toEntity(pilotProfileDTO);
        pilotProfile = pilotProfileRepository.save(pilotProfile);
        return pilotProfileMapper.toDto(pilotProfile);
    }

    @Override
    public Optional<PilotProfileDTO> partialUpdate(PilotProfileDTO pilotProfileDTO) {
        LOG.debug("Request to partially update PilotProfile : {}", pilotProfileDTO);

        return pilotProfileRepository
            .findById(pilotProfileDTO.getId())
            .map(existingPilotProfile -> {
                pilotProfileMapper.partialUpdate(existingPilotProfile, pilotProfileDTO);

                return existingPilotProfile;
            })
            .map(pilotProfileRepository::save)
            .map(pilotProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PilotProfileDTO> findAll() {
        LOG.debug("Request to get all PilotProfiles");
        return pilotProfileRepository.findAll().stream().map(pilotProfileMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<PilotProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pilotProfileRepository.findAllWithEagerRelationships(pageable).map(pilotProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PilotProfileDTO> findOne(Long id) {
        LOG.debug("Request to get PilotProfile : {}", id);
        return pilotProfileRepository.findOneWithEagerRelationships(id).map(pilotProfileMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete PilotProfile : {}", id);
        pilotProfileRepository.deleteById(id);
    }
}
