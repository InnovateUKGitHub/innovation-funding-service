package org.innovateuk.ifs.organisation.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.springframework.stereotype.Service;

/**
 * Contains static matching logic used in @{OrganisationMatchingServiceImpl}.
 */
@Service
public class OrganisationPatternMatcher {

    private static final Log LOG = LogFactory.getLog(OrganisationPatternMatcher.class);

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeMatches(Organisation organisation, OrganisationResource organisationResource) {
        try {
            return organisation.getOrganisationType().getId().equals(organisationResource.getOrganisationType());
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type match", e);
            return false;
        }
    }

    @NotSecured(value = "Is a 'static' comparison function", mustBeSecuredByOtherServices = false)
    public boolean organisationTypeIsResearch(Organisation organisation) {
        try {
            return OrganisationTypeEnum.isResearch(organisation.getOrganisationType().getId());
        }
        catch(NullPointerException e) {
            LOG.trace("NPE when checking organisation type is research", e);
            return false;
        }
    }
}
