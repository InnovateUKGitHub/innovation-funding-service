package org.innovateuk.ifs.analytics.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.analytics.controller.GoogleAnalyticsDataLayerController;
import org.innovateuk.ifs.analytics.service.GoogleAnalyticsDataLayerService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
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
    private static final List<Role> APPLICATION_ROLES = singletonList(Role.LEADAPPLICANT);
    private static final List<Role> PROJECT_ROLES = asList(Role.PARTNER, Role.FINANCE_CONTACT);

    @Mock
    private GoogleAnalyticsDataLayerService googleAnalyticsDataLayerService;

    @Override
    protected GoogleAnalyticsDataLayerController supplyControllerUnderTest() {
        return new GoogleAnalyticsDataLayerController();
    }

    @Test
    public void getCompetitionNameForApplication() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionNameByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/application/{application}/competition-name", APPLICATION_ID))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("application").description("Id of the application")
                        )
                ));
    }

    @Test
    public void getCompetitionName() throws Exception {
        when(googleAnalyticsDataLayerService.getCompetitionName(COMPETITION_ID)).thenReturn(serviceSuccess(competitionName()));

        mockMvc.perform(get("/analytics/competition/{competition}/competition-name", COMPETITION_ID))
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

        mockMvc.perform(get("/analytics/project/{project}/competition-name", PROJECT_ID))
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

        mockMvc.perform(get("/analytics/assessment/{assessment}/competition-name", ASSESSMENT_ID))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                        pathParameters(
                                parameterWithName("assessment").description("Id of the assessment")
                        )
                ));
    }

    @Test
    public void getRolesByApplicationId() throws Exception {
        when(googleAnalyticsDataLayerService.getRolesByApplicationId(APPLICATION_ID)).thenReturn(serviceSuccess(APPLICATION_ROLES));

        mockMvc.perform(get("/analytics/application/{applicationId}/user-roles", APPLICATION_ID))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                                pathParameters(
                                        parameterWithName("applicationId").description("Id of the application")
                                )
                ));
    }

    @Test
    public void getRolesByProjectId() throws Exception {
        when(googleAnalyticsDataLayerService.getRolesByProjectId(PROJECT_ID)).thenReturn(serviceSuccess(PROJECT_ROLES));

        mockMvc.perform(get("/analytics/project/{projectId}/user-roles", PROJECT_ID))
                .andExpect(status().isOk())
                .andDo(document("analytics/{method-name}",
                                pathParameters(
                                         parameterWithName("projectId").description("Id of the project")
                                )
                ));
    }

    private static String competitionName() {
        return "competition name";
    }
}