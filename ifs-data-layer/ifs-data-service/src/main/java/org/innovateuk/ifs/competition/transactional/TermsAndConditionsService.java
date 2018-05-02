package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;

/**
 * Service for operations around the usage and processing of TermsAndConditions
 */
public interface TermsAndConditionsService {

    @NotSecured(value = "Any user can see the latest site terms and conditions",
            mustBeSecuredByOtherServices = false)
    ServiceResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions();

}
