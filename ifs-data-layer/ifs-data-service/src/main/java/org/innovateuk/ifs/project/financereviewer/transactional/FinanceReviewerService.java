package org.innovateuk.ifs.project.financereviewer.transactional;

import org.innovateuk.ifs.activitylog.advice.Activity;
import org.innovateuk.ifs.activitylog.resource.ActivityType;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

/**
 * Service for managing finance reviewers.
 */
public interface FinanceReviewerService {

    @SecuredBySpring(value = "ASSIGN_FINANCE_REVIEWER",
            description = "Only project finance can assign finance reviewers")
    @PreAuthorize("hasAuthority('project_finance')")
    @Activity(type = ActivityType.FINANCE_REVIEWER_ADDED, projectId = "projectId")
    ServiceResult<Long> assignFinanceReviewer(long financeReviewerUserId, long projectId);

    @SecuredBySpring(value = "FIND_FINANCE_USERS",
            description = "Only project finance can see other finance reviewers")
    @PreAuthorize("hasAuthority('project_finance')")
    ServiceResult<List<SimpleUserResource>> findFinanceUsers();

    @SecuredBySpring(value = "GET_FINANCE_USER_FOR_PROJECT",
            description = "Only project finance view a projects finance reviewer")
    @PreAuthorize("hasPermission(#projectId, 'org.innovateuk.ifs.project.resource.ProjectResource', 'VIEW_FINANCE_REVIEWER')")
    ServiceResult<SimpleUserResource> getFinanceReviewerForProject(long projectId);
}
