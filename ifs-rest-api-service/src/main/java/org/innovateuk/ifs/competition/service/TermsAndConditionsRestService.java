package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;

import java.util.List;

/**
 * Interface for CRUD operations on {@link TermsAndConditionsResource} related data.
 */
public interface TermsAndConditionsRestService {

    RestResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions();

    RestResult<GrantTermsAndConditionsResource> getById(Long id);

    RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions();

}
