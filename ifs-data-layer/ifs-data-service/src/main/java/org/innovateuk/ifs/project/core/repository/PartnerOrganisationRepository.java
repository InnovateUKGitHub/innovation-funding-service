package org.innovateuk.ifs.project.core.repository;

import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PartnerOrganisationRepository extends PagingAndSortingRepository<PartnerOrganisation, Long> {
    PartnerOrganisation findOneByProjectIdAndOrganisationId(long projectId, long organisationId);
    void deleteOneByProjectIdAndOrganisationId(long projectId, long organisationId);
    List<PartnerOrganisation> findByProjectId(long projectId);
}
