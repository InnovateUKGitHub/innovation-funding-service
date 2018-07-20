package org.innovateuk.ifs.finance.service;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.innovateuk.ifs.finance.resource.GrantClaimMaximumResource;
import org.springframework.stereotype.Service;

@Service
public class GrantClaimMaximumRestServiceImpl extends BaseRestService implements GrantClaimMaximumRestService {

    private final String grantClaimMaximumRestURL = "/grantClaimMaximum";

    @Override
    public RestResult<GrantClaimMaximumResource> getGrantClaimMaximumById(long id) {
        return getWithRestResult(grantClaimMaximumRestURL + "/" + id, GrantClaimMaximumResource.class);
    }

    @Override
    public RestResult<Void> update(GrantClaimMaximumResource grantClaimMaximumResource) {
        return null;
    }
}
