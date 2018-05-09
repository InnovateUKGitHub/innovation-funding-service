package org.innovateuk.ifs.competition.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.competition.resource.GrantTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.SiteTermsAndConditionsResource;
import org.innovateuk.ifs.competition.resource.TermsAndConditionsResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.grantTermsAndConditionsResourceListType;

/**
 * TermsAndConditionsRestServiceImpl is a utility for CRUD operations on {@link TermsAndConditionsResource}.
 * This class connects to the { org.innovateuk.ifs.competition.controller.TermsAndConditionsController}
 * through a REST call.
 */
@Service
public class TermsAndConditionsRestServiceImpl extends BaseRestService implements TermsAndConditionsRestService {

    private final String termsAndConditionsRestUrl = "/terms-and-conditions";

    @Override
    public RestResult<List<GrantTermsAndConditionsResource>> getLatestVersionsForAllTermsAndConditions() {
        return getWithRestResult(termsAndConditionsRestUrl + "/getLatest", grantTermsAndConditionsResourceListType());
    }

    @Override
    public RestResult<GrantTermsAndConditionsResource> getById(Long id) {
        return getWithRestResult(termsAndConditionsRestUrl + "/getById/" + id, GrantTermsAndConditionsResource.class);
    }

    @Override
    public RestResult<SiteTermsAndConditionsResource> getLatestSiteTermsAndConditions() {
        return getWithRestResultAnonymous(termsAndConditionsRestUrl + "/site", SiteTermsAndConditionsResource.class);
    }
}
