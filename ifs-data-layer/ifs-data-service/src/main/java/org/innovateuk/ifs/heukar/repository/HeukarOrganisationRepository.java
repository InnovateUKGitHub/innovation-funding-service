package org.innovateuk.ifs.heukar.repository;

import org.innovateuk.ifs.heukar.domain.HeukarOrganisationType;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HeukarOrganisationRepository extends CrudRepository<HeukarOrganisationType, Long> {

    public Set<HeukarOrganisationType> findAllByApplicationId(long applicationId);
}
