package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.AssessorDashboardState;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

import java.util.List;
import java.util.Map;

public interface CofunderDashboardService {

    ServiceResult<Map<AssessorDashboardState, List<CofunderDashboardCompetitionResource>>> getCompetitionsForCofunding(long userId);

    ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId);

}
