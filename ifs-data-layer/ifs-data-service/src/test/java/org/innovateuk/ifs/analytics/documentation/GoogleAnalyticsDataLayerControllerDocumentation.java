package org.innovateuk.ifs.analytics.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.analytics.controller.GoogleAnalyticsDataLayerController;
import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GoogleAnalyticsDataLayerControllerDocumentation extends BaseControllerMockMVCTest<GoogleAnalyticsDataLayerController> {

    private static final long APPLICATION_ID = 1L;
    private static final long COMPETITION_ID = 1L;
    private static final long PROJECT_ID = 1L;
    private static final long ASSESSMENT_ID = 1L;
    private static final List<Role> APPLICATION_ROLES = asList(Role.LEADAPPLICANT, Role.COLLABORATOR);
    private static final List<Role> PROJECT_ROLES = asList(Role.PARTNER, Role.FINANCE_CONTACT, Role.PROJECT_MANAGER);

    @Mock
    private GoogleAnalyticsDataLayerService googleAnalyticsDataLayerService;

    @Override
    protected GoogleAnalyticsDataLayerController supplyControllerUnderTest() {
        return new GoogleAnalyticsDataLayerController();
    }

    @Test
    public void getCompetitionNameForApplication() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionNameByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/application/{application}/competition-name", APPLICATION_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("application").description("Id of the application")
                        )
                ));
    }

    @Test
    public void getCompetitionNameForInvite() throws Exception {
        final String inviteHash = new UUID(1L, 1L).toString();

        when(googleAnalyticsDataLayerService.getCompetitionNameByInviteHash(inviteHash)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/invite/{inviteHash}/competition-name", inviteHash)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("inviteHash").description("Hash of the invite")
                        )
                ));
    }

    @Test
    public void getCompetitionName() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionName(COMPETITION_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/competition/{competition}/competition-name", COMPETITION_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("competition").description("Id of the competition")
                        )
                ));
    }

    @Test
    public void getCompetitionNameForProject() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionNameByProjectId(PROJECT_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/project/{project}/competition-name", PROJECT_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("project").description("Id of the project")
                        )
                ));
    }

    @Test
    public void getCompetitionNameForAssessment() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionNameByAssessmentId(ASSESSMENT_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/assessment/{assessment}/competition-name", ASSESSMENT_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("assessment").description("Id of the assessment")
                        )
                ));
    }

    @Test
    public void getRolesByApplicationIdForCurrentUser() throws Exception {
        when(googleAnalyticsDataLayerService.getRolesByApplicationIdForCurrentUser(APPLICATION_ID)).thenReturn(serviceSuccess(APPLICATION_ROLES));

        mockMvc.perform(get("/analytics/application/{applicationId}/user-roles", APPLICATION_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                                pathParameters(
                                        parameterWithName("applicationId").description("Id of the application")
                                )
                ));
    }

    @Test
    public void getRolesByProjectIdForCurrentUser() throws Exception {
        when(googleAnalyticsDataLayerService.getRolesByProjectIdForCurrentUser(PROJECT_ID)).thenReturn(serviceSuccess(PROJECT_ROLES));

        mockMvc.perform(get("/analytics/project/{projectId}/user-roles", PROJECT_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                                pathParameters(
                                         parameterWithName("projectId").description("Id of the project")
                                )
                ));
    }

    @Test
    public void getApplicationIdForProject() throws Exception {
        when(googleAnalyticsDataLayerService.getApplicationIdForProject(PROJECT_ID)).thenReturn(serviceSuccess(APPLICATION_ID));

        mockMvc.perform(get("/analytics/project/{projectId}/application-id", PROJECT_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                                pathParameters(
                                        parameterWithName("projectId").description("Id of the project")
                                )
                ));
    }

    @Test
    public void getApplicationIdForAssessment() throws Exception {
        when(googleAnalyticsDataLayerService.getApplicationIdForAssessment(ASSESSMENT_ID)).thenReturn(serviceSuccess(APPLICATION_ID));

        mockMvc.perform(get("/analytics/assessment/{assessmentId}/application-id", ASSESSMENT_ID)
                .header("IFS_AUTH_TOKEN", "123abc"))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("assessmentId").description("Id of the assessment")
                        )
                ));
    }

    private static String competitionName() {
        return "competition name";
    }
}