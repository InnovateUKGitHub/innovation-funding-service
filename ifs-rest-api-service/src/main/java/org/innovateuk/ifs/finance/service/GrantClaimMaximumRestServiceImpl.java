package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.longsSetType;

@Service
public class GrantClaimMaximumRestServiceImpl extends BaseRestService implements GrantClaimMaximumRestService {

    private static final String grantClaimMaximumRestURL = "/grant-claim-maximum";

    @Override
    public RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id) {
        return getWithRestResult(grantClaimMaximumRestURL + "/" + id, GrantClaimMaximumResource.class);
    }

    @Override
    public RestResult<List<GrantClaimMaximumResource>> getGrantClaimMaximumByCompetitionId(long competitionId) {
        return getWithRestResult(grantClaimMaximumRestURL + "/competition/" + competitionId, new ParameterizedTypeReference<List<GrantClaimMaximumResource>>() {});
    }

    @Override
    public RestResult<Set<Long>> revertToDefaultForCompetitionType(long competitionId) {
        return postWithRestResult(grantClaimMaximumRestURL + "/revert-to-default/" + competitionId, longsSetType());
    }

    @Override
    public RestResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource) {
        return postWithRestResult(grantClaimMaximumRestURL + "/", grantClaimMaximumResource, GrantClaimMaximumResource.class);
    }

    @Override
    public RestResult<Boolean> isMaximumFundingLevelConstant(long competitionId) {
        return getWithRestResult(grantClaimMaximumRestURL + "/maximum-funding-level-overridden/" + competitionId, Boolean.class);
    }
}
