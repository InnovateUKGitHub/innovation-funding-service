package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;

public interface TermsAndConditionsRestService {

    RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions();

}
