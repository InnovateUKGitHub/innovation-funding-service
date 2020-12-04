package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HeukarPartnerOrganisationService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'HEUKAR_PARTNER_ORGANISATION')")
    ServiceResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(long applicationId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'HEUKAR_PARTNER_ORGANISATION')")
    ServiceResult<HeukarPartnerOrganisation> addNewPartnerOrgToApplication(long applicationId, long organisationTypeId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'HEUKAR_PARTNER_ORGANISATION')")
    ServiceResult<HeukarPartnerOrganisation> updatePartnerOrganisation(Long id, long organisationTypeId);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'HEUKAR_PARTNER_ORGANISATION')")
    ServiceResult<Void> deletePartnerOrganisation(Long id);

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'HEUKAR_PARTNER_ORGANISATION')")
    ServiceResult<HeukarPartnerOrganisationResource> findOne(Long id);

}
