package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PendingPartnerProgressRepository extends CrudRepository<PendingPartnerProgress, Long> {

    Optional<PendingPartnerProgress> findByPartnerOrganisationProjectIdAndPartnerOrganisationOrganisationId(long projectId, long organisationId);

    default Optional<PendingPartnerProgress> findByOrganisationIdAndProjectId(long projectId, long organisationId) {
        return findByPartnerOrganisationProjectIdAndPartnerOrganisationOrganisationId(projectId, organisationId);
    }
}
