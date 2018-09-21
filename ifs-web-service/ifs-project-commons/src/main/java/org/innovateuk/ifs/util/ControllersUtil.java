package org.innovateuk.ifs.util;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * utility class for project controllers.
 */
public final class ControllersUtil {

    private ControllersUtil() {}

    public static boolean isLeadPartner(final PartnerOrganisationRestService partnerOrganisationService, final Long projectId, final Long organisationId) {
        RestResult<List<PartnerOrganisationResource>> result = partnerOrganisationService.getProjectPartnerOrganisations(projectId);
        if(null != result && result.isSuccess()) {
            Optional<PartnerOrganisationResource> partnerOrganisationResource = simpleFindFirst(result.getSuccess(), PartnerOrganisationResource::isLeadOrganisation);
            return partnerOrganisationResource.isPresent() && partnerOrganisationResource.get().getOrganisation().equals(organisationId);
        } else {
            return false;
        }
    }
}

