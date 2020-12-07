package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HeukarPartnerOrganisationService {

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(long applicationId);

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<HeukarPartnerOrganisation> addNewPartnerOrgToApplication(long applicationId, long organisationTypeId);

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<HeukarPartnerOrganisation> updatePartnerOrganisation(Long id, long organisationTypeId);

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<Void> deletePartnerOrganisation(Long id);

    @PreAuthorize("hasAuthority('applicant')")
    ServiceResult<HeukarPartnerOrganisationResource> findOne(Long id);

}
