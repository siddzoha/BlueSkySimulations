package com.bluesky.simulations.repository;

import com.bluesky.simulations.domain.PilotProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface PilotProfileRepositoryWithBagRelationships {
    Optional<PilotProfile> fetchBagRelationships(Optional<PilotProfile> pilotProfile);

    List<PilotProfile> fetchBagRelationships(List<PilotProfile> pilotProfiles);

    Page<PilotProfile> fetchBagRelationships(Page<PilotProfile> pilotProfiles);
}
