package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.application.resource.ApplicationAssessorResource;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.domain.CompetitionParticipant;
import org.innovateuk.ifs.invite.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Profile;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.*;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.EnumSet.of;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.REJECT;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.domain.ParticipantStatus.ACCEPTED;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.innovateuk.ifs.user.resource.UserRoleType.*;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapArray;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleToMap;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class ApplicationAssessmentSummaryServiceImplTest extends BaseServiceUnitTest<ApplicationAssessmentSummaryServiceImpl> {

    @Override
    protected ApplicationAssessmentSummaryServiceImpl supplyServiceUnderTest() {
        return new ApplicationAssessmentSummaryServiceImpl();
    }

    @Test
    public void getAssessors() throws Exception {
        Competition competition = newCompetition().build();

        Application application = newApplication()
                .withCompetition(competition)
                .build();

        Profile[] profiles = newProfile().
                withBusinessType(BUSINESS, ACADEMIC, BUSINESS).
                withSkillsAreas("Solar Power, Genetics, Recycling", "Human computer interaction, Wearables, IoT", "Electronic/photonic components").
                withInnovationArea(
                        newInnovationArea()
                                .withId(1L)
                                .withName("Emerging Tech and Industries")
                                .build(),
                        newInnovationArea()
                                .withId(2L)
                                .withName("Robotics and AS")
                                .build(),
                        newInnovationArea()
                                .withId(3L)
                                .withName("Electronics, Sensors and photonics")
                                .build()).
                buildArray(3, Profile.class);

        Map<Long, Profile> participantProfiles = Stream.of(profiles).collect(toMap(Profile::getId, identity()));

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .withUser(newUser()
                        .withId(1L, 2L, 3L)
                        .withFirstName("John", "Dave", "Richard")
                        .withLastName("Barnes", "Smith", "Turner")
                        .withProfileId(simpleMapArray(profiles, Profile::getId, Long.class))
                        .buildArray(3, User.class))
                .withStatus(ACCEPTED)
                .withCompetition(competition)
                .build(3);

        Map<Long, Optional<Assessment>> assessmentsForParticipants = asMap(
                1L,
                // Intentionally leaving the first user without an assessment to make them available
                Optional.empty(),
                2L,
                Optional.of(newAssessment()
                        .withActivityState(buildActivityStateWithState(PENDING))
                        .build()),
                3L,
                Optional.of(newAssessment()
                        .withActivityState(buildActivityStateWithState(REJECTED))
                        .withProcessOutcome(newProcessOutcome()
                                .withOutcomeType(REJECT.getType())
                                .withDescription("Conflict of interest")
                                .withComment("Member of board of directors")
                                .build(1))
                        .build()));

        Map<Long, Long> totalApplicationCountsForParticipants = setUpScoresForParticipants(competitionParticipants);
        Map<Long, Long> assignedCountsForParticipants = setUpScoresForParticipants(competitionParticipants);
        Map<Long, Long> submittedCountsForParticipants = setUpScoresForParticipants(competitionParticipants);

        List<ApplicationAssessorResource> expected = newApplicationAssessorResource()
                .withUserId(1L, 2L, 3L)
                .withFirstName("John", "Dave", "Richard")
                .withLastName("Barnes", "Smith", "Turner")
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS)
                .withInnovationAreas(newInnovationAreaResource()
                                .withId(1L)
                                .withName("Emerging Tech and Industries")
                                .buildSet(1),
                        newInnovationAreaResource()
                                .withId(2L)
                                .withName("Robotics and AS")
                                .buildSet(1),
                        newInnovationAreaResource()
                                .withId(3L)
                                .withName("Electronics, Sensors and photonics")
                                .buildSet(1))
                .withAvailable(true, false, false)
                .withMostRecentAssessmentId(assessmentsForParticipants.get(1L).map(Assessment::getId).orElse(null),
                        assessmentsForParticipants.get(2L).map(Assessment::getId).orElse(null),
                        assessmentsForParticipants.get(3L).map(Assessment::getId).orElse(null))
                .withMostRecentAssessmentState(assessmentsForParticipants.get(1L).map(Assessment::getActivityState).orElse(null),
                        assessmentsForParticipants.get(2L).map(Assessment::getActivityState).orElse(null),
                        assessmentsForParticipants.get(3L).map(Assessment::getActivityState).orElse(null))
                .withTotalApplicationsCount(totalApplicationCountsForParticipants.get(1L),
                        totalApplicationCountsForParticipants.get(2L),
                        totalApplicationCountsForParticipants.get(3L))
                .withAssignedCount(assignedCountsForParticipants.get(1L),
                        assignedCountsForParticipants.get(2L),
                        assignedCountsForParticipants.get(3L))
                .withSubmittedCount(submittedCountsForParticipants.get(1L),
                        submittedCountsForParticipants.get(2L),
                        submittedCountsForParticipants.get(3L))
                .withSkillAreas("Solar Power, Genetics, Recycling", "Human computer interaction, Wearables, IoT", "Electronic/photonic components")
                .withRejectReason(null, null, "Conflict of interest")
                .withRejectComment(null, null, "Member of board of directors")
                .build(3);

        EnumSet<AssessmentStates> assessmentStatesThatAreUnassigned = of(REJECTED, WITHDRAWN);
        EnumSet<AssessmentStates> assessmentStatesThatAreAssigned = EnumSet.complementOf(assessmentStatesThatAreUnassigned);
        EnumSet<AssessmentStates> assessmentStatesThatAreSubmitted = of(SUBMITTED);

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(competitionParticipantRepositoryMock
                .getByCompetitionIdAndRoleAndStatus(competition.getId(), CompetitionParticipantRole.ASSESSOR, ACCEPTED)).thenReturn(competitionParticipants);

        Stream.of(profiles).forEach(profile -> when(profileRepositoryMock.findOne(profile.getId())).thenReturn(profile));

        when(innovationAreaMapperMock.mapToResource(isA(InnovationArea.class)))
                .then(invocation -> {
                    InnovationArea argument = invocation.getArgumentAt(0, InnovationArea.class);
                    return newInnovationAreaResource()
                            .withId(argument.getId())
                            .withName(argument.getName())
                            .build();
                });
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(isA(Long.class), eq(application.getId())))
                .then(invocation -> assessmentsForParticipants.get(invocation.getArgumentAt(0, Long.class)));

        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateStateNotIn(
                isA(Long.class), eq(getBackingStates(assessmentStatesThatAreUnassigned))))
                .then(invocation -> totalApplicationCountsForParticipants.get(invocation.getArgumentAt(0, Long.class)));

        when(assessmentRepositoryMock.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(
                isA(Long.class), eq(competition.getId()), eq(getBackingStates(assessmentStatesThatAreAssigned))))
                .then(invocation -> assignedCountsForParticipants.get(invocation.getArgumentAt(0, Long.class)));

        when(assessmentRepositoryMock.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(
                isA(Long.class), eq(competition.getId()), eq(getBackingStates(assessmentStatesThatAreSubmitted))))
                .then(invocation -> submittedCountsForParticipants.get(invocation.getArgumentAt(0, Long.class)));

        List<ApplicationAssessorResource> found = service.getAssessors(application.getId()).getSuccessObjectOrThrowException();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, competitionParticipantRepositoryMock, innovationAreaMapperMock, assessmentRepositoryMock, profileRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(application.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByCompetitionIdAndRoleAndStatus(competition.getId(), CompetitionParticipantRole.ASSESSOR, ACCEPTED);
        competitionParticipants.forEach(competitionParticipant -> {
            Long userId = competitionParticipant.getUser().getId();
            Long profileId = competitionParticipant.getUser().getProfileId();
            inOrder.verify(assessmentRepositoryMock)
                    .findFirstByParticipantUserIdAndTargetIdOrderByIdDesc(userId, application.getId());
            inOrder.verify(profileRepositoryMock).findOne(profileId);
            participantProfiles.get(profileId).getInnovationAreas().forEach(
                    innovationArea -> inOrder.verify(innovationAreaMapperMock).mapToResource(innovationArea));
            inOrder.verify(assessmentRepositoryMock)
                    .countByParticipantUserIdAndActivityStateStateNotIn(userId, getBackingStates(assessmentStatesThatAreUnassigned));
            inOrder.verify(assessmentRepositoryMock)
                    .countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(userId, competition.getId(), getBackingStates(assessmentStatesThatAreAssigned));
            inOrder.verify(assessmentRepositoryMock)
                    .countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(userId, competition.getId(), getBackingStates(assessmentStatesThatAreSubmitted));
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Organisation[] organisations = newOrganisation()
                .withName("Acme Ltd.", "IO systems", "Liquid Dynamics", "Piezo Electrics")
                .buildArray(4, Organisation.class);

        Application application = newApplication()
                .withName("Progressive machines")
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, COLLABORATOR, LEADAPPLICANT, COMP_ADMIN)
                        .withOrganisationId(simpleMapArray(organisations, Organisation::getId, Long.class))
                        .buildArray(4, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"))
                .build();

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        Stream.of(organisations)
                .forEach(organisation -> when(organisationRepositoryMock.findOne(organisation.getId())).thenReturn(organisation));

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccessObjectOrThrowException();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, organisationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(application.getId());
        inOrder.verify(organisationRepositoryMock).findOne(organisations[0].getId());
        inOrder.verify(organisationRepositoryMock).findOne(organisations[1].getId());
        inOrder.verifyNoMoreInteractions();
    }

    private ActivityState buildActivityStateWithState(AssessmentStates state) {
        return new ActivityState(APPLICATION_ASSESSMENT, state.getBackingState());
    }

    private Map<Long, Long> setUpScoresForParticipants(List<CompetitionParticipant> competitionParticipants) {
        Random random = new Random();
        return simpleToMap(competitionParticipants, competitionParticipant -> competitionParticipant.getUser().getId(), competitionParticipant -> random.nextLong());
    }
}