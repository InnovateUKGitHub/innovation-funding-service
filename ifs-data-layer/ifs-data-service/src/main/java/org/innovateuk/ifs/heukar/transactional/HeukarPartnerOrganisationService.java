package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HeukarPartnerOrganisationService {

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "READ", description = "An applicant can view the partner organisations for their application")
    ServiceResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(long applicationId);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "CREATE", description = "An applicant can create partner organisations for their application")
    ServiceResult<HeukarPartnerOrganisation> addNewPartnerOrgToApplication(long applicationId, long organisationTypeId);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "UPDATE", description = "An applicant can make changes to the partner organisations for their application")
    ServiceResult<HeukarPartnerOrganisation> updatePartnerOrganisation(Long id, long organisationTypeId);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "DELETE", description = "An applicant can make changes to the partner organisations for their application")
    ServiceResult<Void> deletePartnerOrganisation(Long id);

    @PreAuthorize("hasAuthority('applicant')")
    @SecuredBySpring(value = "READ", description = "An applicant can view the partner organisations for their application")
    ServiceResult<HeukarPartnerOrganisationResource> findOne(Long id);

}
