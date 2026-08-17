package com.bluesky.simulations.service.impl;

import com.bluesky.simulations.domain.Aircraft;
import com.bluesky.simulations.repository.AircraftRepository;
import com.bluesky.simulations.service.AircraftService;
import com.bluesky.simulations.service.dto.AircraftDTO;
import com.bluesky.simulations.service.mapper.AircraftMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bluesky.simulations.domain.Aircraft}.
 */
@Service
@Transactional
public class AircraftServiceImpl implements AircraftService {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftServiceImpl.class);

    private final AircraftRepository aircraftRepository;

    private final AircraftMapper aircraftMapper;

    public AircraftServiceImpl(AircraftRepository aircraftRepository, AircraftMapper aircraftMapper) {
        this.aircraftRepository = aircraftRepository;
        this.aircraftMapper = aircraftMapper;
    }

    @Override
    public AircraftDTO save(AircraftDTO aircraftDTO) {
        LOG.debug("Request to save Aircraft : {}", aircraftDTO);
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDTO);
        aircraft = aircraftRepository.save(aircraft);
        return aircraftMapper.toDto(aircraft);
    }

    @Override
    public AircraftDTO update(AircraftDTO aircraftDTO) {
        LOG.debug("Request to update Aircraft : {}", aircraftDTO);
        Aircraft aircraft = aircraftMapper.toEntity(aircraftDTO);
        aircraft = aircraftRepository.save(aircraft);
        return aircraftMapper.toDto(aircraft);
    }

    @Override
    public Optional<AircraftDTO> partialUpdate(AircraftDTO aircraftDTO) {
        LOG.debug("Request to partially update Aircraft : {}", aircraftDTO);

        return aircraftRepository
            .findById(aircraftDTO.getId())
            .map(existingAircraft -> {
                aircraftMapper.partialUpdate(existingAircraft, aircraftDTO);

                return existingAircraft;
            })
            .map(aircraftRepository::save)
            .map(aircraftMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AircraftDTO> findAll() {
        LOG.debug("Request to get all Aircrafts");
        return aircraftRepository.findAll().stream().map(aircraftMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AircraftDTO> findOne(Long id) {
        LOG.debug("Request to get Aircraft : {}", id);
        return aircraftRepository.findById(id).map(aircraftMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Aircraft : {}", id);
        aircraftRepository.deleteById(id);
    }
}
