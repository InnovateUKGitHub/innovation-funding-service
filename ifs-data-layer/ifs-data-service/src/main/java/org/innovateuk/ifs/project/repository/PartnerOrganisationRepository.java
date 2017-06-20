package org.innovateuk.ifs.project.repository;

import org.innovateuk.ifs.project.domain.PartnerOrganisation;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface PartnerOrganisationRepository extends PagingAndSortingRepository<PartnerOrganisation, Long> {
    PartnerOrganisation findOneByProjectIdAndOrganisationId(Long projectId, Long organisationId);
    List<PartnerOrganisation> findByProjectId(Long projectId);
}
