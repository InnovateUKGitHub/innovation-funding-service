package org.innovateuk.ifs.analytics.service;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface GoogleAnalyticsDataLayerService {

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByApplicationId(long applicationId);

    @PreAuthorize("permitAll")
    ServiceResult<String> getCompetitionName(long competitionId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByProjectId(long projectId);

    @SecuredBySpring(value = "READ", description = "Only those with either comp admin or project finance roles can read the status related to setup")
    @PreAuthorize("isAuthenticated()")
    ServiceResult<String> getCompetitionNameByAssessmentId(long assessmentId);
}
