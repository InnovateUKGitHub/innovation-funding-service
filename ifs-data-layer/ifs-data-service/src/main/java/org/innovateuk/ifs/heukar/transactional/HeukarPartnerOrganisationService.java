package org.innovateuk.ifs.heukar.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.heukar.domain.HeukarPartnerOrganisation;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface HeukarPartnerOrganisationService {

    @PreAuthorize("hasPermission(#applicationId, 'org.innovateuk.ifs.application.resource.ApplicationResource', 'READ')")
    ServiceResult<List<HeukarPartnerOrganisationResource>> findByApplicationId(long applicationId);

    ServiceResult<HeukarPartnerOrganisation> addNewPartnerOrgToApplication(long applicationId, long organisationTypeId);

    ServiceResult<HeukarPartnerOrganisation> updatePartnerOrganisation(Long id);

    ServiceResult<Void> deletePartnerOrganisation(Long id);

    ServiceResult<HeukarPartnerOrganisationResource> findOne(Long id);

}
