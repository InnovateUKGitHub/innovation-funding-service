package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

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
}