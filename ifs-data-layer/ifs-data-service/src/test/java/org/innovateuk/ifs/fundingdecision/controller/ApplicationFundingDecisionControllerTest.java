package org.innovateuk.ifs.fundingdecision.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.fundingdecision.transactional.ApplicationFundingService;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ApplicationFundingDecisionControllerTest extends BaseControllerMockMVCTest<ApplicationFundingDecisionController> {

    @Mock
    private ApplicationFundingService applicationFundingServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private CompetitionService competitionServiceMock;

    @Mock
    private ProjectService projectServiceMock;

    @Override
    protected ApplicationFundingDecisionController supplyControllerUnderTest() {
        return new ApplicationFundingDecisionController();
    }

    @Test
    public void testSaveApplicationFundingDecisionData() throws Exception {
        Long competitionId = 1L;
        Map<Long, FundingDecision> decision = asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED);

        when(applicationFundingServiceMock.saveFundingDecisionData(competitionId, decision)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/1")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(objectMapper.writeValueAsString(decision)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(applicationFundingServiceMock).saveFundingDecisionData(competitionId, decision);
    }

    @Test
    public void testSendNotificationsShouldReturnAppropriateStatusCode() throws Exception {

        Map<Long, FundingDecision> decisions = asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        ApplicationResource application = newApplicationResource().withCompetition(4L).build();

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        when(applicationServiceMock.getApplicationById(1L)).thenReturn(serviceSuccess(application));
        when(competitionServiceMock.getCompetitionById(4L)).thenReturn(serviceSuccess(competition));
        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyApplicantsOfFundingDecisions(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(projectServiceMock, times(1)).createProjectsFromFundingDecisions(decisions);
        verify(applicationFundingServiceMock, times(1)).notifyApplicantsOfFundingDecisions(notification);
    }

    @Test
    public void testSendNotificationsForCompetitionWithReleaseFeedbackCompletionStage() throws Exception {

        Map<Long, FundingDecision> decisions = asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        ApplicationResource application = newApplicationResource().withCompetition(4L).build();

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .build();

        when(applicationServiceMock.getApplicationById(1L)).thenReturn(serviceSuccess(application));
        when(competitionServiceMock.getCompetitionById(4L)).thenReturn(serviceSuccess(competition));
        when(applicationFundingServiceMock.notifyApplicantsOfFundingDecisions(notification)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(projectServiceMock, never()).createProjectsFromFundingDecisions(decisions);
        verify(applicationFundingServiceMock, times(1)).notifyApplicantsOfFundingDecisions(notification);
    }

    @Test
    public void testSendNotificationsButErrorOccursCreatingProjects() throws Exception {

        Map<Long, FundingDecision> decisions = asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        ApplicationResource application = newApplicationResource().withCompetition(4L).build();

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        when(applicationServiceMock.getApplicationById(1L)).thenReturn(serviceSuccess(application));
        when(competitionServiceMock.getCompetitionById(4L)).thenReturn(serviceSuccess(competition));
        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceFailure(internalServerErrorError()));

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError()))));

        verify(projectServiceMock, times(1)).createProjectsFromFundingDecisions(decisions);
        verify(applicationFundingServiceMock, never()).notifyApplicantsOfFundingDecisions(any(FundingNotificationResource.class));
    }

    @Test
    public void testSendNotificationsButErrorOccursSendingNotifications() throws Exception {

        Map<Long, FundingDecision> decisions = asMap(1L, FundingDecision.FUNDED, 2L, FundingDecision.UNFUNDED, 3L, FundingDecision.ON_HOLD);
        FundingNotificationResource notification = new FundingNotificationResource("Body of notification message.", decisions);

        ApplicationResource application = newApplicationResource().withCompetition(4L).build();

        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();

        when(applicationServiceMock.getApplicationById(1L)).thenReturn(serviceSuccess(application));
        when(competitionServiceMock.getCompetitionById(4L)).thenReturn(serviceSuccess(competition));
        when(projectServiceMock.createProjectsFromFundingDecisions(decisions)).thenReturn(serviceSuccess());
        when(applicationFundingServiceMock.notifyApplicantsOfFundingDecisions(notification)).thenReturn(serviceFailure(internalServerErrorError()));

        mockMvc.perform(post("/applicationfunding/sendNotifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(notification)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json(toJson(new RestErrorResponse(internalServerErrorError()))));

        verify(projectServiceMock, times(1)).createProjectsFromFundingDecisions(decisions);
        verify(applicationFundingServiceMock, times(1)).notifyApplicantsOfFundingDecisions(notification);
    }
}
