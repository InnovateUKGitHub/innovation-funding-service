package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;

import java.util.Set;

public interface GrantClaimMaximumRestService {

    RestResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource);

    RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id);

    RestResult<Set<Long>> getGrantClaimMaximumsForCompetitionType(long competititionTypeId);

    RestResult<Set<Long>> getGrantClaimMaximumsForCompetition(long competititionId);
}
