package org.innovateuk.ifs.affiliation.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.user.resource.AffiliationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.affiliationResourceListType;

/**
 * AffiliationRestServiceImpl is a utility for CRUD operations on {@link AffiliationResource}.
 * This class connects to the {org.innovateuk.ifs.affiliation.controller.AffiliationController}
 * through a REST call.
 */
@Service
public class AffiliationRestServiceImpl extends BaseRestService implements AffiliationRestService {

    private String profileRestURL = "/affiliation";

    @Override
    public RestResult<List<AffiliationResource>> getUserAffiliations(Long userId) {
        return getWithRestResult(format("%s/id/%s/getUserAffiliations", profileRestURL, userId), affiliationResourceListType());
    }

    @Override
    public RestResult<Void> updateUserAffiliations(Long userId, List<AffiliationResource> affiliations) {
        return putWithRestResult(format("%s/id/%s/updateUserAffiliations", profileRestURL, userId), affiliations, Void.class);
    }
}
