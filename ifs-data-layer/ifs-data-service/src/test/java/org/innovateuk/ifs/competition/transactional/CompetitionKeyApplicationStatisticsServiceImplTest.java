package org.innovateuk.ifs.competition.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.*;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.REJECTED;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;
import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CompetitionKeyApplicationStatisticsServiceImplTest extends
        BaseServiceUnitTest<CompetitionKeyApplicationStatisticsServiceImpl> {

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private ApplicationRepository applicationRepositoryMock;

    @Mock
    private ApplicationStatisticsRepository applicationStatisticsRepositoryMock;

    @Override
    protected CompetitionKeyApplicationStatisticsServiceImpl supplyServiceUnderTest() {
        return new CompetitionKeyApplicationStatisticsServiceImpl();
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() throws Exception {
        Long competitionId = 1L;
        CompetitionOpenKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyApplicationStatisticsResource()
                        .withApplicationsPastHalf(1)
                        .withApplicationsPerAssessor(2)
                        .withApplicationsStarted(3)
                        .withApplicationsSubmitted(4)
                        .build();

        Competition competition = newCompetition()
                .withAssessorCount(4)
                .build();

        BigDecimal limit = new BigDecimal(50L);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(applicationRepositoryMock
                .countByCompetitionIdAndApplicationProcessActivityStateInAndCompletionLessThanEqual(competitionId,
                        CREATED_AND_OPEN_STATUSES, limit)).thenReturn(keyStatisticsResource.getApplicationsStarted());
        when(applicationRepositoryMock
                .countByCompetitionIdAndApplicationProcessActivityStateNotInAndCompletionGreaterThan(competitionId,
                        SUBMITTED_AND_INELIGIBLE_STATES, limit)).thenReturn(keyStatisticsResource
                .getApplicationsPastHalf());
        when(applicationRepositoryMock.countByCompetitionIdAndApplicationProcessActivityStateIn(competitionId,
                SUBMITTED_AND_INELIGIBLE_STATES)).thenReturn(keyStatisticsResource.getApplicationsSubmitted());

        CompetitionOpenKeyApplicationStatisticsResource response = service.getOpenKeyStatisticsByCompetition
                (competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() throws Exception {
        long competitionId = 1L;

        CompetitionClosedKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyApplicationStatisticsResource()
                        .withApplicationsPerAssessor(2)
                        .withApplicationsRequiringAssessors(2)
                        .withAssignmentCount(3)
                        .build();

        Competition competition = newCompetition()
                .withAssessorCount(2)
                .build();

        List<Assessment> assessments = newAssessment()
                .withProcessState(AssessmentState.PENDING, REJECTED, AssessmentState.OPEN)
                .build(3);

        List<Assessment> assessmentList = newAssessment()
                .withProcessState(AssessmentState.SUBMITTED)
                .build(1);

        List<ApplicationStatistics> applicationStatistics = newApplicationStatistics()
                .withAssessments(assessments, assessmentList, emptyList())
                .build(3);

        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);
        when(applicationStatisticsRepositoryMock.findByCompetitionAndApplicationProcessActivityStateIn(competitionId,
                SUBMITTED_STATES)).thenReturn(applicationStatistics);

        CompetitionClosedKeyApplicationStatisticsResource response = service.getClosedKeyStatisticsByCompetition
                (competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);

    }

    @Test
    public void getFundedKeyStatisticsByCompetition() {
        long competitionId = 1L;
        int applicationsNotifiedOfDecision = 1;
        int applicationsAwaitingDecision = 2;

        List<Application> applications = newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withFundingDecision(FundingDecisionStatus.FUNDED, FundingDecisionStatus.UNFUNDED,
                        FundingDecisionStatus.ON_HOLD)
                .build(3);

        when(applicationRepositoryMock.findByCompetitionIdAndApplicationProcessActivityStateIn(competitionId,
                SUBMITTED_STATES)).thenReturn(applications);
        when(applicationRepositoryMock
                .countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNotNull(competitionId))
                .thenReturn(applicationsNotifiedOfDecision);
        when(applicationRepositoryMock.countByCompetitionIdAndFundingDecisionIsNotNullAndManageFundingEmailDateIsNull
                (competitionId)).thenReturn(applicationsAwaitingDecision);

        CompetitionFundedKeyApplicationStatisticsResource response = service.getFundedKeyStatisticsByCompetition
                (competitionId).getSuccess();

        assertEquals(3, response.getApplicationsSubmitted());
        assertEquals(1, response.getApplicationsFunded());
        assertEquals(1, response.getApplicationsNotFunded());
        assertEquals(1, response.getApplicationsOnHold());
        assertEquals(applicationsNotifiedOfDecision, response.getApplicationsNotifiedOfDecision());
        assertEquals(applicationsAwaitingDecision, response.getApplicationsAwaitingDecision());
    }
}