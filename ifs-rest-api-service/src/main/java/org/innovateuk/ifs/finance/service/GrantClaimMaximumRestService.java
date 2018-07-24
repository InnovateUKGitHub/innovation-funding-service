package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;

import java.util.Set;

public interface GrantClaimMaximumRestService {

    RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id);

    RestResult<GrantClaimMaximumResource> save(GrantClaimMaximumResource grantClaimMaximumResource);
}
