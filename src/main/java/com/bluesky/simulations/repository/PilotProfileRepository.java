package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.PilotProfile;
import com.bluesky.simulations.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the PilotProfile entity.
 *
 * When extending this class, extend PilotProfileRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface PilotProfileRepository extends PilotProfileRepositoryWithBagRelationships, JpaRepository<PilotProfile, Long> {
    default Optional<PilotProfile> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findOneWithToOneRelationships(id));
    }

    default List<PilotProfile> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships());
    }

    default Page<PilotProfile> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAllWithToOneRelationships(pageable));
    }

    @Query(
        value = "select pilotProfile from PilotProfile pilotProfile left join fetch pilotProfile.user",
        countQuery = "select count(pilotProfile) from PilotProfile pilotProfile"
    )
    Page<PilotProfile> findAllWithToOneRelationships(Pageable pageable);

    @Query("select pilotProfile from PilotProfile pilotProfile left join fetch pilotProfile.user")
    List<PilotProfile> findAllWithToOneRelationships();

    @Query("select pilotProfile from PilotProfile pilotProfile left join fetch pilotProfile.user where pilotProfile.id =:id")
    Optional<PilotProfile> findOneWithToOneRelationships(@Param("id") Long id);

    @EntityGraph(attributePaths = { "achievementses" })
    Optional<PilotProfile> findByUser(User user);
}
