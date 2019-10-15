package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.PendingPartnerProgress;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PendingPartnerProgressRepository extends CrudRepository<PendingPartnerProgress, Long> {

    Optional<PendingPartnerProgress> findByPartnerOrganisationProjectIdAndPartnerOrganisationOrganisationId(long organisationId, long projectId);
}
