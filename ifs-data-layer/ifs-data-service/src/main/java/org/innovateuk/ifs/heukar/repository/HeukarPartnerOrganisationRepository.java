package org.innovateuk.ifs.heukar.repository;

import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.springframework.data.repository.CrudRepository;

import java.util.Set;

public interface HeukarPartnerOrganisationRepository extends CrudRepository<HeukarPartnerOrganisation, Long> {

    public Set<HeukarPartnerOrganisation> findAllByApplicationId(long applicationId);
}
