package org.innovateuk.ifs.fundingdecision.transactional;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.application.resource.ApplicationExpressionOfInterestConfigResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.Decision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
import org.innovateuk.ifs.application.transactional.ApplicationEoiService;
import org.innovateuk.ifs.application.transactional.ApplicationService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.project.core.transactional.ProjectToBeCreatedService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.Decision.FUNDED;
import static org.innovateuk.ifs.application.resource.Decision.UNFUNDED;
import static org.innovateuk.ifs.application.resource.Decision.EOI_APPROVED;
import static org.innovateuk.ifs.application.resource.Decision.EOI_REJECTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationFundingNotificationBulkServiceImplTest {

    @InjectMocks
    private ApplicationFundingNotificationBulkServiceImpl service;

    @Mock
    private CompetitionService competitionService;

    @Mock
    private ApplicationFundingService applicationFundingService;

    @Mock
    private ProjectToBeCreatedService projectToBeCreatedService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationEoiService applicationEoiService;

    @Test
    public void sendBulkFundingNotifications() {
        String messageBody = "MyMessage";
        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        long successfulApplicationId = 1L;
        long unsuccessfulApplicationId = 2L;
        ApplicationResource application = newApplicationResource()
                .withId(successfulApplicationId)
                .build();
        Map<Long, Decision> decisions = ImmutableMap.<Long, Decision> builder()
            .put(successfulApplicationId, FUNDED)
            .put(unsuccessfulApplicationId, UNFUNDED)
            .build();
        FundingNotificationResource resource = new FundingNotificationResource(messageBody, decisions);
        FundingNotificationResource unfundedResource = new FundingNotificationResource(messageBody,
                ImmutableMap.<Long, Decision> builder()
                .put(unsuccessfulApplicationId, UNFUNDED)
                .build());

        when(competitionService.getCompetitionByApplicationId(successfulApplicationId)).thenReturn(serviceSuccess(competition));
        when(applicationFundingService.notifyApplicantsOfDecisions(unfundedResource)).thenReturn(serviceSuccess());
        when(projectToBeCreatedService.markApplicationReadyToBeCreated(successfulApplicationId, messageBody)).thenReturn(serviceSuccess());
        when(applicationService.getApplicationById(successfulApplicationId)).thenReturn(serviceSuccess(application));

        ServiceResult<Void> result = service.sendBulkFundingNotifications(resource);

        assertTrue(result.isSuccess());

        verify(applicationFundingService).notifyApplicantsOfDecisions(unfundedResource);
        verify(projectToBeCreatedService).markApplicationReadyToBeCreated(successfulApplicationId, messageBody);
    }

    @Test
    public void sendBulkEoiNotifications() {
        String messageBody = "MyMessageForEoi";
        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .withEnabledForExpressionOfInterest(true)
                .build();
        long successfulApplicationId = 1L;
        long unsuccessfulApplicationId = 2L;

        ApplicationResource application = newApplicationResource()
                .withId(successfulApplicationId)
                .withApplicationExpressionOfInterestConfigResource(ApplicationExpressionOfInterestConfigResource.builder()
                        .enabledForExpressionOfInterest(true)
                        .build())
                .build();
        Map<Long, Decision> decisions = ImmutableMap.<Long, Decision> builder()
                .put(successfulApplicationId, EOI_APPROVED)
                .put(unsuccessfulApplicationId, EOI_REJECTED)
                .build();
        FundingNotificationResource resource = new FundingNotificationResource(messageBody, decisions);
        FundingNotificationResource fundedResource = new FundingNotificationResource(messageBody,
                ImmutableMap.<Long, Decision> builder()
                        .put(successfulApplicationId, EOI_APPROVED)
                        .build());
        FundingNotificationResource unfundedResource = new FundingNotificationResource(messageBody,
                ImmutableMap.<Long, Decision> builder()
                        .put(unsuccessfulApplicationId, EOI_REJECTED)
                        .build());

        when(competitionService.getCompetitionByApplicationId(successfulApplicationId)).thenReturn(serviceSuccess(competition));
        when(applicationFundingService.notifyApplicantsOfDecisions(fundedResource)).thenReturn(serviceSuccess());
        when(applicationFundingService.notifyApplicantsOfDecisions(unfundedResource)).thenReturn(serviceSuccess());
        when(applicationEoiService.createFullApplicationFromEoi(successfulApplicationId)).thenReturn(serviceSuccess(anyLong()));
        when(applicationService.getApplicationById(successfulApplicationId)).thenReturn(serviceSuccess(application));

        ServiceResult<Void> result = service.sendBulkFundingNotifications(resource);

        assertTrue(result.isSuccess());

        verify(applicationFundingService).notifyApplicantsOfDecisions(fundedResource);
        verify(applicationFundingService).notifyApplicantsOfDecisions(unfundedResource);
        verify(applicationEoiService).createFullApplicationFromEoi(successfulApplicationId);
        verifyNoInteractions(projectToBeCreatedService);
    }

    @Test
    public void sendBulkFundingNotifications_completionStageBeforePS() {
        String messageBody = "MyMessage";
        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .build();
        long successfulApplicationId = 1L;
        long unsuccessfulApplicationId = 2L;
        Map<Long, Decision> decisions = ImmutableMap.<Long, Decision> builder()
                .put(successfulApplicationId, FUNDED)
                .put(unsuccessfulApplicationId, UNFUNDED)
                .build();
        FundingNotificationResource resource = new FundingNotificationResource(messageBody, decisions);

        when(competitionService.getCompetitionByApplicationId(successfulApplicationId)).thenReturn(serviceSuccess(competition));
        when(applicationFundingService.notifyApplicantsOfDecisions(resource)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendBulkFundingNotifications(resource);

        assertTrue(result.isSuccess());

        verify(applicationFundingService).notifyApplicantsOfDecisions(resource);
    }

}
