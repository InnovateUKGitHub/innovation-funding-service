package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

@Service
public class CofunderDashboardRestServiceImpl extends BaseRestService implements CofunderDashboardRestService {

    private final String baseUrl = "/cofunder/dashboard";

    @Override
    public RestResult<CofunderDashboardApplicationPageResource> getCofunderDashboard(long userId, long competitionId, int page) {
        return getWithRestResult(String.format("%s/user/%d/competition/%d?page=%d", baseUrl, userId, competitionId, page), CofunderDashboardApplicationPageResource.class);
    }
}
