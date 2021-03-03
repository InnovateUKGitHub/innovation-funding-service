package org.innovateuk.ifs.fundingdecision.transactional;

import com.google.common.collect.ImmutableMap;
import org.innovateuk.ifs.application.resource.FundingDecision;
import org.innovateuk.ifs.application.resource.FundingNotificationResource;
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

import static org.innovateuk.ifs.application.resource.FundingDecision.FUNDED;
import static org.innovateuk.ifs.application.resource.FundingDecision.UNFUNDED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void sendBulkFundingNotifications() {
        String messageBody = "MyMessage";
        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.PROJECT_SETUP)
                .build();
        long successfulApplicationId = 1L;
        long unsuccessfulApplicationId = 2L;
        Map<Long, FundingDecision> decisions = ImmutableMap.<Long, FundingDecision> builder()
            .put(successfulApplicationId, FUNDED)
            .put(unsuccessfulApplicationId, UNFUNDED)
            .build();
        FundingNotificationResource resource = new FundingNotificationResource(messageBody, decisions);
        FundingNotificationResource unfundedResource = new FundingNotificationResource(messageBody,
                ImmutableMap.<Long, FundingDecision> builder()
                .put(unsuccessfulApplicationId, UNFUNDED)
                .build());

        when(competitionService.getCompetitionByApplicationId(successfulApplicationId)).thenReturn(serviceSuccess(competition));
        when(applicationFundingService.notifyApplicantsOfFundingDecisions(unfundedResource)).thenReturn(serviceSuccess());
        when(projectToBeCreatedService.markApplicationReadyToBeCreated(successfulApplicationId, messageBody)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendBulkFundingNotifications(resource);

        assertTrue(result.isSuccess());

        verify(applicationFundingService).notifyApplicantsOfFundingDecisions(unfundedResource);
        verify(projectToBeCreatedService).markApplicationReadyToBeCreated(successfulApplicationId, messageBody);
    }

    @Test
    public void sendBulkFundingNotifications_completionStageBeforePS() {
        String messageBody = "MyMessage";
        CompetitionResource competition = newCompetitionResource()
                .withCompletionStage(CompetitionCompletionStage.RELEASE_FEEDBACK)
                .build();
        long successfulApplicationId = 1L;
        long unsuccessfulApplicationId = 2L;
        Map<Long, FundingDecision> decisions = ImmutableMap.<Long, FundingDecision> builder()
                .put(successfulApplicationId, FUNDED)
                .put(unsuccessfulApplicationId, UNFUNDED)
                .build();
        FundingNotificationResource resource = new FundingNotificationResource(messageBody, decisions);

        when(competitionService.getCompetitionByApplicationId(successfulApplicationId)).thenReturn(serviceSuccess(competition));
        when(applicationFundingService.notifyApplicantsOfFundingDecisions(resource)).thenReturn(serviceSuccess());

        ServiceResult<Void> result = service.sendBulkFundingNotifications(resource);

        assertTrue(result.isSuccess());

        verify(applicationFundingService).notifyApplicantsOfFundingDecisions(resource);
    }

}
