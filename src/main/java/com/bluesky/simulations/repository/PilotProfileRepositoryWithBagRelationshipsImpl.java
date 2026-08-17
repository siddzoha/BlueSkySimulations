package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.PilotProfile;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class PilotProfileRepositoryWithBagRelationshipsImpl implements PilotProfileRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String PILOTPROFILES_PARAMETER = "pilotProfiles";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<PilotProfile> fetchBagRelationships(Optional<PilotProfile> pilotProfile) {
        return pilotProfile.map(this::fetchAchievementses);
    }

    @Override
    public Page<PilotProfile> fetchBagRelationships(Page<PilotProfile> pilotProfiles) {
        return new PageImpl<>(
            fetchBagRelationships(pilotProfiles.getContent()),
            pilotProfiles.getPageable(),
            pilotProfiles.getTotalElements()
        );
    }

    @Override
    public List<PilotProfile> fetchBagRelationships(List<PilotProfile> pilotProfiles) {
        return Optional.of(pilotProfiles).map(this::fetchAchievementses).orElse(List.of());
    }

    PilotProfile fetchAchievementses(PilotProfile result) {
        return entityManager
            .createQuery(
                "select pilotProfile from PilotProfile pilotProfile left join fetch pilotProfile.achievementses where pilotProfile.id = :id",
                PilotProfile.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<PilotProfile> fetchAchievementses(List<PilotProfile> pilotProfiles) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, pilotProfiles.size()).forEach(index -> order.put(pilotProfiles.get(index).getId(), index));
        List<PilotProfile> result = entityManager
            .createQuery(
                "select pilotProfile from PilotProfile pilotProfile left join fetch pilotProfile.achievementses where pilotProfile in :pilotProfiles",
                PilotProfile.class
            )
            .setParameter(PILOTPROFILES_PARAMETER, pilotProfiles)
            .getResultList();
        result.sort((o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
