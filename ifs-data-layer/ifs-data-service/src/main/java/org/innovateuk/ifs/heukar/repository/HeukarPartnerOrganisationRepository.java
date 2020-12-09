package org.innovateuk.ifs.heukar.repository;

import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HeukarPartnerOrganisationRepository extends CrudRepository<HeukarPartnerOrganisation, Long> {

    public List<HeukarPartnerOrganisation> findAllByApplicationId(long applicationId);
}
