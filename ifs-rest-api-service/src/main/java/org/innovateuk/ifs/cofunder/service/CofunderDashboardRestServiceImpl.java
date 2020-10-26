package org.innovateuk.ifs.cofunder.service;

import org.innovateuk.ifs.cofunder.resource.AssessorDashboardState;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.mapAssessorDashboardStateToCofunderDashboardCompetitionResourceListType;

@Service
public class CofunderDashboardRestServiceImpl extends BaseRestService implements CofunderDashboardRestService {

    private final String baseUrl = "/cofunder/dashboard";

    @Override
    public RestResult<Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>>> getCofunderCompetitionDashboard(long userId) {
        return getWithRestResult(String.format("%s/user/%d/dashboard", baseUrl, userId), mapAssessorDashboardStateToCofunderDashboardCompetitionResourceListType());
    }

    @Override
    public RestResult<CofunderDashboardApplicationPageResource> getCofunderCompetitionDashboardApplications(long userId, long competitionId, int page) {
        return getWithRestResult(String.format("%s/user/%d/competition/%d?page=%d", baseUrl, userId, competitionId, page), CofunderDashboardApplicationPageResource.class);
    }
}
