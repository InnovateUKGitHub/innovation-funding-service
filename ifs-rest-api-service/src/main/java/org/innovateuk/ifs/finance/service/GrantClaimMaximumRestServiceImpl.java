package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsSetType;

@Service
public class GrantClaimMaximumRestServiceImpl extends BaseRestService implements GrantClaimMaximumRestService {

    private final String grantClaimMaximumRestURL = "/grantClaimMaximum";

    @Override
    public RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id) {
        return getWithRestResult(grantClaimMaximumRestURL + "/" + id, GrantClaimMaximumResource.class);
    }

    @Override
    public RestResult<Set<Long>> getGrantClaimMaximumsForCompetitionType(long competititionTypeId) {
        return getWithRestResult(grantClaimMaximumRestURL + "/getForCompetitionType/" + competititionTypeId, longsSetType());
    }

    @Override
    public RestResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        return postWithRestResult(grantClaimMaximumRestURL + "/", grantClaimMaximumResource, GrantClaimMaximumResource.class);
    }
}
