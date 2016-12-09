package com.worth.ifs.project.util;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.project.PartnerOrganisationService;
import com.worth.ifs.project.resource.PartnerOrganisationResource;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * utility class for project controllers.
 */
public class ControllersUtil {

    public static boolean isLeadPartner(final PartnerOrganisationService partnerOrganisationService, final Long projectId, final Long organisationId) {
        ServiceResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getPartnerOrganisations(projectId);
        if(null != result && result.isSuccess()) {
            Optional<PartnerOrganisationResource> partnerOrganisationResource = simpleFindFirst(result.getSuccessObject(), PartnerOrganisationResource::isLeadOrganisation);
            return partnerOrganisationResource.isPresent() && partnerOrganisationResource.get().getOrganisation().equals(organisationId);
        } else {
            return false;
        }
    }
}
