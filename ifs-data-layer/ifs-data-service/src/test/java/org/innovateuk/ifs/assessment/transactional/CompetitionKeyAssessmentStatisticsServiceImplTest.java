package org.innovateuk.ifs.assessment.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.assessment.domain.AssessmentParticipant;
import org.innovateuk.ifs.assessment.repository.AssessmentInviteRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentParticipantRepository;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.user.resource.UserStatus;
import org.junit.Test;
import org.mockito.Mock;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static java.util.EnumSet.complementOf;
import static java.util.EnumSet.of;
import static org.innovateuk.ifs.assessment.builder.AssessmentParticipantBuilder.newAssessmentParticipant;
import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
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

    @Override
    protected CompetitionKeyAssessmentStatisticsServiceImpl supplyServiceUnderTest() {
        return new CompetitionKeyAssessmentStatisticsServiceImpl();
    }

    @Test
    public void getReadyToOpenKeyStatisticsByCompetition() {
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
    public void getOpenKeyStatisticsByCompetition() {
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
        when(competitionRepositoryMock.findById(competitionId)).thenReturn(Optional.of(competition));

        CompetitionOpenKeyAssessmentStatisticsResource response = service.getOpenKeyStatisticsByCompetition
                (competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }

    @Test
    public void getClosedKeyStatisticsByCompetition() {
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
    public void getInAssessmentKeyStatisticsByCompetition() {
        long competitionId = 1L;

        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssessmentsStarted(1)
                .withAssessmentsSubmitted(2)
                .withAssignmentCount(3)
                .withAssignmentsAccepted(4)
                .withAssignmentsWaiting(5)
                .build();


        when(assessmentRepositoryMock.countByActivityStateInAndTargetCompetitionIdAndParticipantUserStatusIn(
                complementOf(of(REJECTED, WITHDRAWN)), competitionId, singletonList(UserStatus.ACTIVE))).thenReturn(keyStatisticsResource.getAssignmentCount());
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionIdAndParticipantUserStatusIn(PENDING, competitionId, singletonList(UserStatus.ACTIVE))).thenReturn
                (keyStatisticsResource.getAssignmentsWaiting());
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionIdAndParticipantUserStatusIn(ACCEPTED, competitionId, singletonList(UserStatus.ACTIVE))).thenReturn
                (keyStatisticsResource.getAssignmentsAccepted());
        when(assessmentRepositoryMock.countByActivityStateInAndTargetCompetitionIdAndParticipantUserStatusIn(of(OPEN,
                DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT), competitionId, singletonList(UserStatus.ACTIVE))).thenReturn(keyStatisticsResource
                .getAssessmentsStarted());
        when(assessmentRepositoryMock.countByActivityStateAndTargetCompetitionIdAndParticipantUserStatusIn(SUBMITTED, competitionId, singletonList(UserStatus.ACTIVE)))
                .thenReturn(keyStatisticsResource.getAssessmentsSubmitted());

        CompetitionInAssessmentKeyAssessmentStatisticsResource response = service
                .getInAssessmentKeyStatisticsByCompetition(competitionId).getSuccess();
        assertEquals(keyStatisticsResource, response);
    }
}