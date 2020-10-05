package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.transactional.BaseTransactionalService;

public class CofunderDashboardServiceImpl extends BaseTransactionalService implements CofunderDashboardService {

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getCompetitionsForCofunding(long userId) {
        return null;
    }

    @Override
    public ServiceResult<CofunderDashboardCompetitionResource> getApplicationsForCofunding(long userId, long competitionId) {
        return null;
    }
}
