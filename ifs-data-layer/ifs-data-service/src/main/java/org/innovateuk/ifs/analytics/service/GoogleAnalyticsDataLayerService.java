package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.resource.Role;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface GoogleAnalyticsDataLayerService {

    @SecuredBySpring(value = "READ", description = "Any authenticated user can get any competition name by application ID")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByApplicationId(long applicationId);

    @SecuredBySpring(value = "READ", description = "Any anonymous or authenticated user can get any competition name by project ID")
    @PreAuthorize("permitAll")
    ServiceResult<String> getCompetitionName(long competitionId);

    @SecuredBySpring(value = "READ", description = "Any authenticated user can get any competition name by project ID")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByProjectId(long projectId);

    @SecuredBySpring(value = "READ", description = "Any authenticated user can get any competition name by assessment ID")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId);

    @SecuredBySpring(value = "READ", description = "Any authenticated user can see their roles on an application")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<List<Role>> getRolesByApplicationIdForCurrentUser(long applicationId);


    @SecuredBySpring(value = "READ", description = "Any authenticated user can see their roles on a project")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<List<Role>> getRolesByProjectIdForCurrentUser(long projectId);
}