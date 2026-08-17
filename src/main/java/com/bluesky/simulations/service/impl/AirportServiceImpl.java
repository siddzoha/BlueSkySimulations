package com.bluesky.simulations.service.impl;

import com.bluesky.simulations.domain.Airport;
import com.bluesky.simulations.repository.AirportRepository;
import com.bluesky.simulations.service.AirportService;
import com.bluesky.simulations.service.dto.AirportDTO;
import com.bluesky.simulations.service.mapper.AirportMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.bluesky.simulations.domain.Airport}.
 */
@Service
@Transactional
public class AirportServiceImpl implements AirportService {

    private static final Logger LOG = LoggerFactory.getLogger(AirportServiceImpl.class);

    private final AirportRepository airportRepository;

    private final AirportMapper airportMapper;

    public AirportServiceImpl(AirportRepository airportRepository, AirportMapper airportMapper) {
        this.airportRepository = airportRepository;
        this.airportMapper = airportMapper;
    }

    @Override
    public AirportDTO save(AirportDTO airportDTO) {
        LOG.debug("Request to save Airport : {}", airportDTO);
        Airport airport = airportMapper.toEntity(airportDTO);
        airport = airportRepository.save(airport);
        return airportMapper.toDto(airport);
    }

    @Override
    public AirportDTO update(AirportDTO airportDTO) {
        LOG.debug("Request to update Airport : {}", airportDTO);
        Airport airport = airportMapper.toEntity(airportDTO);
        airport = airportRepository.save(airport);
        return airportMapper.toDto(airport);
    }

    @Override
    public Optional<AirportDTO> partialUpdate(AirportDTO airportDTO) {
        LOG.debug("Request to partially update Airport : {}", airportDTO);

        return airportRepository
            .findById(airportDTO.getId())
            .map(existingAirport -> {
                airportMapper.partialUpdate(existingAirport, airportDTO);

                return existingAirport;
            })
            .map(airportRepository::save)
            .map(airportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AirportDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Airports");
        return airportRepository.findAll(pageable).map(airportMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AirportDTO> findOne(Long id) {
        LOG.debug("Request to get Airport : {}", id);
        return airportRepository.findById(id).map(airportMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Airport : {}", id);
        airportRepository.deleteById(id);
    }
}
