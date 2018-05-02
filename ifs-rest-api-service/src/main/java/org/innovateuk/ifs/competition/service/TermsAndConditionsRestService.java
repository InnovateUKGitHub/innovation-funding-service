package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;

/**
 * Interface for CRUD operations on {@link org.innovateuk.ifs.competition.resource.TermsAndConditionsResource} related data.
 */
public interface TermsAndConditionsRestService {

    RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions();

}
