package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;

public interface GrantClaimMaximumRestService {

    RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id);

    RestResult<Void> update(GrantClaimMaximumResource grantClaimMaximumResource);
}
