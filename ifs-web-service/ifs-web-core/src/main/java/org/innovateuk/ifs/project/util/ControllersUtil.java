package org.innovateuk.ifs.project.util;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * utility class for project controllers.
 */
public class ControllersUtil {

    public static boolean isLeadPartner(final PartnerOrganisationRestService partnerOrganisationService, final Long projectId, final Long organisationId) {
        RestResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getProjectPartnerOrganisations(projectId);
        if(null != result && result.isSuccess()) {
            Optional<PartnerOrganisationResource> partnerOrganisationResource = simpleFindFirst(result.getSuccessObject(), PartnerOrganisationResource::isLeadOrganisation);
            return partnerOrganisationResource.isPresent() && partnerOrganisationResource.get().getOrganisation().equals(organisationId);
        } else {
            return false;
        }
    }
}

