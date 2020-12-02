package org.innovateuk.ifs.supporter.service;

import org.innovateuk.ifs.supporter.resource.AssessorDashboardState;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.springframework.web.bind.annotation.PathVariable;


import java.util.List;
import java.util.Map;

public interface SupporterDashboardRestService {
    RestResult<Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>>> getSupporterCompetitionDashboard(long userId);
    RestResult<SupporterDashboardApplicationPageResource> getSupporterCompetitionDashboardApplications(@PathVariable long userId, @PathVariable long competitionId, int page);
}