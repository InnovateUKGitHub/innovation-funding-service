package org.innovateuk.ifs.cofunder.transactional;

import org.innovateuk.ifs.cofunder.resource.CofunderDashboardApplicationPageResource;
import org.innovateuk.ifs.cofunder.resource.CofunderDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CofunderDashboardService {
    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CAN_VIEW_OWN_DASHBOARD')")
    ServiceResult<CofunderDashboardCompetitionResource> getCompetitionsForCofunding(long userId);

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CAN_VIEW_OWN_DASHBOARD')")
    ServiceResult<CofunderDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable);

}
