package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;

import java.util.List;
import java.util.Set;

public interface GrantClaimMaximumRestService {

    RestResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource);

    RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id);

    RestResult<List<GrantClaimMaximumResource>> getGrantClaimMaximumByCompetitionId(long competitionId);

    RestResult<Set<Long>> revertToDefaultForCompetitionType(long competitionId);

    RestResult<Boolean> isMaximumFundingLevelConstant(long competitionId);
}
