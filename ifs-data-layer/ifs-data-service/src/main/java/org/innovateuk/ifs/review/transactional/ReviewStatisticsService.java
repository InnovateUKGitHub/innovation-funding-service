package org.innovateuk.ifs.review.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

/**
 * Service to get statistics related to Review Panels.
 */
@Service
public interface ReviewStatisticsService {
    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "READ_ASSESSMENT_PANEL_KEY_STATISTICS",
            description = "Comp admins and project finance users can see key statistics for the assessment review panel page")
    ServiceResult<ReviewKeyStatisticsResource> getReviewPanelKeyStatistics(long competitionId);

    @PreAuthorize("hasAnyAuthority('comp_admin', 'project_finance')")
    @SecuredBySpring(
            value = "READ_REVIEW_INVITE_STATISTICS",
            description = "Comp admins and project finance users can see invite statistics for the assessment review panel invite page")
    ServiceResult<ReviewInviteStatisticsResource> getReviewInviteStatistics(long competitionId);
}