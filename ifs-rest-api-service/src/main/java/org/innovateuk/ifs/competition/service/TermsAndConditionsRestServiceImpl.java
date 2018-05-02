package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.springframework.stereotype.Service;

@Service
public class TermsAndConditionsRestServiceImpl extends BaseRestService implements TermsAndConditionsRestService {

    @Override
    public RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return getWithRestResultAnonymous("/terms-and-conditions/site", SiteTermsAndConditionsResource.class);
    }
}
