package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.supporter.resource.AssessorDashboardState;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.commons.service.BaseRestService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.service.ParameterizedTypeReferences.mapAssessorDashboardStateToSupporterDashboardCompetitionResourceListType;

@Service
public class SupporterDashboardRestServiceImpl extends BaseRestService implements SupporterDashboardRestService {

    private final String baseUrl = "/supporter/dashboard";

    @Override
    public RestResult<Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>>> getSupporterCompetitionDashboard(long userId) {
        return getWithRestResult(String.format("%s/user/%d/dashboard", baseUrl, userId), mapAssessorDashboardStateToSupporterDashboardCompetitionResourceListType());
    }

    @Override
    public RestResult<SupporterDashboardApplicationPageResource> getSupporterCompetitionDashboardApplications(long userId, long competitionId, int page) {
        return getWithRestResult(String.format("%s/user/%d/competition/%d?page=%d", baseUrl, userId, competitionId, page), SupporterDashboardApplicationPageResource.class);
    }
}
