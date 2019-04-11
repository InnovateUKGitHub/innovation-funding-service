package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.AffiliationListResource;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

/**
 * AffiliationRestServiceImpl is a utility for CRUD operations on {@link AffiliationResource}.
 * This class connects to the {org.innovateuk.ifs.affiliation.controller.AffiliationController}
 * through a REST call.
 */
@Service
public class AffiliationRestServiceImpl extends BaseRestService implements AffiliationRestService {

    private String profileRestURL = "/affiliation";

    @Override
    public RestResult<AffiliationListResource> getUserAffiliations(long userId) {
        return getWithRestResult(format("%s/id/%s/getUserAffiliations", profileRestURL, userId), AffiliationListResource.class);
    }

    @Override
    public RestResult<Void> updateUserAffiliations(long userId, AffiliationListResource affiliations) {
        return putWithRestResult(format("%s/id/%s/updateUserAffiliations", profileRestURL, userId), affiliations, Void.class);
    }
}
