package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.ApplicationStatistics;
import org.innovateuk.ifs.application.repository.ApplicationStatisticsRepository;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.List;

import static java.util.EnumSet.of;
import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.application.transactional.ApplicationSummaryServiceImpl.SUBMITTED_STATES;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.competition.domain.CompetitionParticipantRole.ASSESSOR;
import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class CompetitionKeyAssessmentStatisticsServiceImplTest extends
        BaseServiceUnitTest<CompetitionKeyAssessmentStatisticsServiceImpl> {

    @Mock
    private AssessmentInviteRepository assessmentInviteRepositoryMock;

    @Mock
    private AssessmentParticipantRepository assessmentParticipantRepositoryMock;

    @Mock
    private CompetitionRepository competitionRepositoryMock;

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @Mock
    private ApplicationStatisticsRepository applicationStatisticsRepositoryMock;

    @Override
    protected CompetitionKeyAssessmentStatisticsServiceImpl supplyServiceUnderTest() {
        return new CompetitionKeyAssessmentStatisticsServiceImpl();
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() throws Exception {
        Long competitionId = 1L;
        CompetitionReadyToOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionReadyToOpenKeyAssessmentStatisticsResource()
                        .withAssessorsAccepted(1)
                        .withAssessorsInvited(2)
                        .build();

        when(assessmentInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)))
                .thenReturn(keyStatisticsResource.getAssessorsInvited());
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR,
                ParticipantStatus.ACCEPTED)).thenReturn(keyStatisticsResource.getAssessorsAccepted());

        CompetitionReadyToOpenKeyAssessmentStatisticsResource response = service
                .getReadyToOpenKeyStatisticsByCompetition(competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }

    @Test
    public void getOpenKeyStatisticsByCompetition() throws Exception {
        Long competitionId = 1L;
        CompetitionOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyAssessmentStatisticsResource()
                        .withAssessorsAccepted(1)
                        .withAssessorsInvited(2)
                        .build();

        Competition competition = newCompetition()
                .withAssessorCount(4)
                .build();

        when(assessmentInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)))
                .thenReturn(keyStatisticsResource.getAssessorsInvited());
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR,
                ParticipantStatus.ACCEPTED)).thenReturn(keyStatisticsResource.getAssessorsAccepted());
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(competition);

        CompetitionOpenKeyAssessmentStatisticsResource response = service.getOpenKeyStatisticsByCompetition
                (competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() throws Exception {
        long competitionId = 1L;

        CompetitionClosedKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(5)
                        .withAssessorsAccepted(6)
                        .withAssessorsWithoutApplications(2)
                        .build();

        List<AssessmentParticipant> competitionParticipants = newAssessmentParticipant()
                .withId(1L, 2L, 3L)
                .build(3);
        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateNotIn(1L, of(REJECTED, WITHDRAWN)))
                .thenReturn(0L);
        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateNotIn(2L, of(REJECTED, WITHDRAWN)))
                .thenReturn(2L);
        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateNotIn(3L, of(REJECTED, WITHDRAWN)))
                .thenReturn(0L);

        when(assessmentParticipantRepositoryMock.getByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR,
                ParticipantStatus.ACCEPTED)).thenReturn(competitionParticipants);
        when(assessmentInviteRepositoryMock.countByCompetitionIdAndStatusIn(competitionId, EnumSet.of(OPENED, SENT)))
                .thenReturn(keyStatisticsResource.getAssessorsInvited());
        when(assessmentParticipantRepositoryMock.countByCompetitionIdAndRoleAndStatus(competitionId, ASSESSOR,
                ParticipantStatus.ACCEPTED)).thenReturn(keyStatisticsResource.getAssessorsAccepted());

        CompetitionClosedKeyAssessmentStatisticsResource response = service.getClosedKeyStatisticsByCompetition
                (competitionId)
                .getSuccess();
        assertEquals(keyStatisticsResource, response);

    }

    @Test
    public void getInAssessmentKeyStatisticsByCompetition() throws Exception {
        long competitionId = 1L;

        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssessmentsStarted(1)
                .withAssessmentsSubmitted(2)
                .withAssignmentCount(3)
                .withAssignmentsAccepted(4)
                .withAssignmentsWaiting(5)
                .build();

        List<Assessment> assessments = newAssessment()
                .withProcessState(AssessmentState.PENDING, REJECTED, AssessmentState.OPEN)
                .build(3);

        List<Assessment> assessmentList = newAssessment()
                .withProcessState(AssessmentState.SUBMITTED)
                .build(1);

        List<ApplicationStatistics> applicationStatistics = newApplicationStatistics()
                .withAssessments(assessments, assessmentList)
                .build(2);

        when(applicationStatisticsRepositoryMock.findByCompetitionAndApplicationProcessActivityStateIn(competitionId,
                SUBMITTED_STATES)).thenReturn(applicationStatistics);
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionId(PENDING, competitionId)).thenReturn
                (keyStatisticsResource.getAssignmentsWaiting());
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionId(ACCEPTED, competitionId)).thenReturn
                (keyStatisticsResource.getAssignmentsAccepted());
        when(assessmentRepositoryMock.countByActivityStateInAndTargetCompetitionId(of(OPEN,
                DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT), competitionId)).thenReturn(keyStatisticsResource
                .getAssessmentsStarted());
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionId(SUBMITTED, competitionId))
                .thenReturn(keyStatisticsResource.getAssessmentsSubmitted());

        CompetitionInAssessmentKeyAssessmentStatisticsResource response = service
                .getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }
}