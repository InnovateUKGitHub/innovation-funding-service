package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;

public interface CofunderDashboardService {

    ServiceResult<CofunderDashboardCompetitionResource> getCompetitionsForCofunding(long userId);

    ServiceResult<CofunderDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable);

}
