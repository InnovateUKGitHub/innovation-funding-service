package org.innovateuk.ifs.supporter.transactional;

import org.innovateuk.ifs.supporter.resource.AssessorDashboardState;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardApplicationPageResource;
import org.innovateuk.ifs.supporter.resource.SupporterDashboardCompetitionResource;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface SupporterDashboardService {

    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CAN_VIEW_OWN_DASHBOARD')")
    ServiceResult<Map<AssessorDashboardState, List<SupporterDashboardCompetitionResource>>> getCompetitionsForCofunding(long userId);
    @PreAuthorize("hasPermission(#userId, 'org.innovateuk.ifs.user.resource.UserResource', 'CAN_VIEW_OWN_DASHBOARD')")
    ServiceResult<SupporterDashboardApplicationPageResource> getApplicationsForCofunding(long userId, long competitionId, Pageable pageable);

}
