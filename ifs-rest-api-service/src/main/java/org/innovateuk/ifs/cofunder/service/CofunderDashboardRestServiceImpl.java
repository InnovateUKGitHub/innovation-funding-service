package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Service
public class CofunderDashboardRestServiceImpl extends BaseRestService implements CofunderDashboardRestService {

    private final String baseUrl = "/assessment/user/%d";

    @Override
    public RestResult<CofunderDashboardCompetitionResource> getCofunderCompetitionDashboard(long userId) {
        return getWithRestResult(format(baseUrl + "/dashboard", userId), CofunderDashboardCompetitionResource.class);
    }
}
