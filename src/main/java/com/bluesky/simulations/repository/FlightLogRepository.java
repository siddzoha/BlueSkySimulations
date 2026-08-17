package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.FlightLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FlightLog entity.
 */
@Repository
public interface FlightLogRepository extends JpaRepository<FlightLog, Long> {
    default Optional<FlightLog> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<FlightLog> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<FlightLog> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select flightLog from FlightLog flightLog left join fetch flightLog.aircraft left join fetch flightLog.departureAirport left join fetch flightLog.arrivalAirport",
        countQuery = "select count(flightLog) from FlightLog flightLog"
    )
    Page<FlightLog> findAllWithToOneRelationships(Pageable pageable);

    @Query(
        "select flightLog from FlightLog flightLog left join fetch flightLog.aircraft left join fetch flightLog.departureAirport left join fetch flightLog.arrivalAirport"
    )
    List<FlightLog> findAllWithToOneRelationships();

    @Query(
        "select flightLog from FlightLog flightLog left join fetch flightLog.aircraft left join fetch flightLog.departureAirport left join fetch flightLog.arrivalAirport where flightLog.id =:id"
    )
    Optional<FlightLog> findOneWithToOneRelationships(@Param("id") Long id);
}
