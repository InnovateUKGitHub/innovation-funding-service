package org.innovateuk.ifs.analytics.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

public class GoogleAnalyticsDataLayerControllerTest extends BaseControllerMockMVCTest<GoogleAnalyticsDataLayerController> {

    @Mock
    private GoogleAnalyticsDataLayerService googleAnalyticsDataLayerServiceMock;

    @Override
    protected GoogleAnalyticsDataLayerController supplyControllerUnderTest() { return new GoogleAnalyticsDataLayerController(); }

    @Test
    public void getCompetitionNameForApplication() throws Exception {
        final long applicationId=7L;
        final String competitionName="Competition Name";

        when(googleAnalyticsDataLayerServiceMock.getCompetitionNameByApplicationId(applicationId))
                .thenReturn(serviceSuccess(competitionName));

        mockMvc.perform(get("/analytics/application/{applicationId}/competition-name", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(competitionName)));

        verify(googleAnalyticsDataLayerServiceMock, only()).getCompetitionNameByApplicationId(applicationId);
    }

    @Test
    public void getCompetitionName() throws Exception {
        final long competitionId=7L;
        final String competitionName="Competition Name";

        when(googleAnalyticsDataLayerServiceMock.getCompetitionName(competitionId))
                .thenReturn(serviceSuccess(competitionName));

        mockMvc.perform(get("/analytics/competition/{competitionId}/competition-name", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(competitionName)));

        verify(googleAnalyticsDataLayerServiceMock, only()).getCompetitionName(competitionId);
    }

    @Test
    public void getCompetitionNameForProject() throws Exception {
        final long projectId=7L;
        final String competitionName="Competition Name";

        when(googleAnalyticsDataLayerServiceMock.getCompetitionNameByProjectId(projectId))
                .thenReturn(serviceSuccess(competitionName));

        mockMvc.perform(get("/analytics/project/{projectId}/competition-name", projectId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(competitionName)));

        verify(googleAnalyticsDataLayerServiceMock, only()).getCompetitionNameByProjectId(projectId);
    }

    @Test
    public void getCompetitionNameForAssessment() throws Exception {
        final long assessmentId=7L;
        final String competitionName="Competition Name";

        when(googleAnalyticsDataLayerServiceMock.getCompetitionNameByAssessmentId(assessmentId))
                .thenReturn(serviceSuccess(competitionName));

        mockMvc.perform(get("/analytics/assessment/{assessmentId}/competition-name", assessmentId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(competitionName)));

        verify(googleAnalyticsDataLayerServiceMock, only()).getCompetitionNameByAssessmentId(assessmentId);
    }

    @Test
    public void getApplicationRolesById() throws Exception {
        final long applicationId = 12L;
        final Role role = Role.LEADAPPLICANT;

        when(googleAnalyticsDataLayerServiceMock.getRolesByApplicationIdForCurrentUser(applicationId))
                .thenReturn(serviceSuccess(singletonList(role)));

        mockMvc.perform(get("/analytics/application/{applicationId}/user-roles", applicationId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(singletonList(role))));
    }

    @Test
    public void getProjectRolesById() throws Exception {
        final long projectId = 112L;
        final List<Role> roles = asList(Role.PARTNER, Role.PROJECT_MANAGER);

        when(googleAnalyticsDataLayerServiceMock.getRolesByProjectIdForCurrentUser(projectId))
                .thenReturn(serviceSuccess(roles));

        mockMvc.perform(get("/analytics/project/{projectId}/user-roles", projectId))
                .andExpect(status().isOk())
                .andExpect(content().string(toJson(roles)));
    }
}