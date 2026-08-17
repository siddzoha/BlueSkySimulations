package com.bluesky.simulations.service.impl;

import com.bluesky.simulations.domain.FlightLog;
import com.bluesky.simulations.repository.FlightLogRepository;
import com.bluesky.simulations.service.FlightLogService;
import com.bluesky.simulations.service.dto.FlightLogDTO;
import com.bluesky.simulations.service.mapper.FlightLogMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bluesky.simulations.domain.FlightLog}.
 */
@Service
@Transactional
public class FlightLogServiceImpl implements FlightLogService {

    private static final Logger LOG = LoggerFactory.getLogger(FlightLogServiceImpl.class);

    private final FlightLogRepository flightLogRepository;

    private final FlightLogMapper flightLogMapper;

    public FlightLogServiceImpl(FlightLogRepository flightLogRepository, FlightLogMapper flightLogMapper) {
        this.flightLogRepository = flightLogRepository;
        this.flightLogMapper = flightLogMapper;
    }

    @Override
    public FlightLogDTO save(FlightLogDTO flightLogDTO) {
        LOG.debug("Request to save FlightLog : {}", flightLogDTO);
        FlightLog flightLog = flightLogMapper.toEntity(flightLogDTO);
        flightLog = flightLogRepository.save(flightLog);
        return flightLogMapper.toDto(flightLog);
    }

    @Override
    public FlightLogDTO update(FlightLogDTO flightLogDTO) {
        LOG.debug("Request to update FlightLog : {}", flightLogDTO);
        FlightLog flightLog = flightLogMapper.toEntity(flightLogDTO);
        flightLog = flightLogRepository.save(flightLog);
        return flightLogMapper.toDto(flightLog);
    }

    @Override
    public Optional<FlightLogDTO> partialUpdate(FlightLogDTO flightLogDTO) {
        LOG.debug("Request to partially update FlightLog : {}", flightLogDTO);

        return flightLogRepository
            .findById(flightLogDTO.getId())
            .map(existingFlightLog -> {
                flightLogMapper.partialUpdate(existingFlightLog, flightLogDTO);

                return existingFlightLog;
            })
            .map(flightLogRepository::save)
            .map(flightLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FlightLogDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all FlightLogs");
        return flightLogRepository.findAll(pageable).map(flightLogMapper::toDto);
    }

    public Page<FlightLogDTO> findAllWithEagerRelationships(Pageable pageable) {
        return flightLogRepository.findAllWithEagerRelationships(pageable).map(flightLogMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlightLogDTO> findOne(Long id) {
        LOG.debug("Request to get FlightLog : {}", id);
        return flightLogRepository.findOneWithEagerRelationships(id).map(flightLogMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlightLog : {}", id);
        flightLogRepository.deleteById(id);
    }
}
