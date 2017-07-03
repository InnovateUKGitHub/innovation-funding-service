package org.innovateuk.ifs.application.mapper;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;

import java.util.EnumSet;

import static java.util.Optional.of;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;


public class ApplicationAssessorMapperTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ApplicationAssessorMapperImpl applicationAssessorMapper;

    @Test
    public void mapToResource() {

        Competition competition = newCompetition().build();

        Profile profile = newProfile()
                .withBusinessType(BUSINESS)
                .withSkillsAreas("Solar Power, Genetics, Recycling")
                .withInnovationArea(
                        newInnovationArea()
                                .withId(1L)
                                .withName("Emerging Tech and Industries")
                                .build())
                .build();

        CompetitionParticipant competitionParticipant = newCompetitionParticipant()
                .withUser(newUser()
                        .withId(1L)
                        .withFirstName("John")
                        .withLastName("Barnes")
                        .withProfileId(profile.getId())
                        .build())
                .withStatus(ACCEPTED)
                .withCompetition(competition)
                .build();

        Assessment assessment = newAssessment()
                .withActivityState(buildActivityStateWithState(REJECTED))
                .withRejection(newAssessmentRejectOutcome()
                        .withRejectReason(CONFLICT_OF_INTEREST)
                        .withRejectComment("Member of board of directors")
                        .build())
                .build();

        long unassignedCount = 2L;
        long assignedCount = 3L;
        long submittedCount = 4L;

        when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile);

        when(innovationAreaMapperMock.mapToResource(isA(InnovationArea.class)))
                .then(invocation -> {
                    InnovationArea argument = invocation.getArgumentAt(0, InnovationArea.class);
                    return newInnovationAreaResource()
                            .withId(argument.getId())
                            .withName(argument.getName())
                            .build();
                });

        EnumSet<AssessmentStates> assessmentStatesThatAreUnassigned = EnumSet.of(REJECTED, WITHDRAWN);
        EnumSet<AssessmentStates> assessmentStatesThatAreAssigned = EnumSet.complementOf(assessmentStatesThatAreUnassigned);
        EnumSet<AssessmentStates> assessmentStatesThatAreSubmitted = EnumSet.of(SUBMITTED);

        ApplicationAssessorResource expected = newApplicationAssessorResource()
                .withUserId(1L)
                .withFirstName("John")
                .withLastName("Barnes")
                .withBusinessType(BUSINESS)
                .withInnovationAreas(newInnovationAreaResource()
                        .withId(1L)
                        .withName("Emerging Tech and Industries")
                        .buildSet(1))
                .withAvailable(false)
                .withMostRecentAssessmentId(assessment.getId())
                .withMostRecentAssessmentState(assessment.getActivityState())
                .withTotalApplicationsCount(unassignedCount)
                .withAssignedCount(assignedCount)
                .withSubmittedCount(submittedCount)
                .withSkillAreas("Solar Power, Genetics, Recycling")
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("Member of board of directors")
                .build();

        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateStateNotIn(
                1L,
                getBackingStates(assessmentStatesThatAreUnassigned)))
                .thenReturn(unassignedCount);

        when(assessmentRepositoryMock.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(
                1L,
                competition.getId(),
                getBackingStates(assessmentStatesThatAreAssigned)))
                .thenReturn(assignedCount);

        when(assessmentRepositoryMock.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(
                1L,
                competition.getId(),
                getBackingStates(assessmentStatesThatAreSubmitted)))
                .thenReturn(submittedCount);


        ApplicationAssessorResource result = applicationAssessorMapper.mapToResource(competitionParticipant, of(assessment));

        assertEquals(expected, result);

        InOrder inOrder = inOrder(competitionParticipantRepositoryMock, innovationAreaMapperMock, assessmentRepositoryMock, profileRepositoryMock);

        Long userId = competitionParticipant.getUser().getId();
        Long profileId = competitionParticipant.getUser().getProfileId();
        inOrder.verify(profileRepositoryMock).findOne(profileId);
        profile.getInnovationAreas().forEach(
                innovationArea -> inOrder.verify(innovationAreaMapperMock).mapToResource(innovationArea));
        inOrder.verify(assessmentRepositoryMock)
                .countByParticipantUserIdAndActivityStateStateNotIn(userId, getBackingStates(assessmentStatesThatAreUnassigned));
        inOrder.verify(assessmentRepositoryMock)
                .countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(userId, competition.getId(), getBackingStates(assessmentStatesThatAreAssigned));
        inOrder.verify(assessmentRepositoryMock)
                .countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(userId, competition.getId(), getBackingStates(assessmentStatesThatAreSubmitted));

    }

    private ActivityState buildActivityStateWithState(AssessmentStates state) {
        return new ActivityState(APPLICATION_ASSESSMENT, state.getBackingState());
    }
}
