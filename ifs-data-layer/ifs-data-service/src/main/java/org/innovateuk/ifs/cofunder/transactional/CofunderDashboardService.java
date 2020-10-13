package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;

public interface CofunderDashboardService {

    ServiceResult<CofunderDashboardCompetitionResource> getCompetitionsForCofunding(long userId);

    ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId);

}
