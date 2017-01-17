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
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InOrder;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static org.innovateuk.ifs.application.builder.ApplicationAssessmentSummaryResourceBuilder.newApplicationAssessmentSummaryResource;
import static org.innovateuk.ifs.application.builder.ApplicationAssessorResourceBuilder.newApplicationAssessorResource;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.CompetitionParticipantBuilder.newCompetitionParticipant;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.PENDING;
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

        List<CompetitionParticipant> competitionParticipants = newCompetitionParticipant()
                .withUser(newUser()
                        .withId(1L, 2L, 3L)
                        .withFirstName("John", "Dave", "Richard")
                        .withLastName("Barnes", "Smith", "Turner")
                        .withInnovationArea(newInnovationArea()
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
                                        .build())
                        .withProfile(newProfile()
                                        .withBusinessType(BUSINESS)
                                        .withSkillsAreas("Solar Power, Genetics, Recycling")
                                        .build(),
                                newProfile()
                                        .withBusinessType(ACADEMIC)
                                        .withSkillsAreas("Human computer interaction, Wearables, IoT")
                                        .build(),
                                newProfile()
                                        .withBusinessType(BUSINESS)
                                        .withSkillsAreas("Electronic/photonic components")
                                        .build()
                        )
                        .buildArray(3, User.class))
                .withStatus(ACCEPTED)
                .withCompetition(competition)
                .build(3);

        Map<Long, Assessment> assessmentsForUsers = asMap(
                // Intentionally leaving the first user without an assessment to make them available
                competitionParticipants.get(1).getUser().getId(),
                newAssessment()
                        .withActivityState(buildActivityStateWithState(PENDING))
                        .build(),
                competitionParticipants.get(2).getUser().getId(),
                newAssessment()
                        .withActivityState(buildActivityStateWithState(OPEN))
                        .build());

        List<ApplicationAssessorResource> expected = newApplicationAssessorResource()
                .withUserId(1L, 2L, 3L)
                .withFirstName("John", "Dave", "Richard")
                .withLastName("Barnes", "Smith", "Turner")
                .withBusinessType(BUSINESS, ACADEMIC, BUSINESS)
                .withInnovationAreas(newInnovationAreaResource()
                                .withId(1L)
                                .withName("Emerging Tech and Industries")
                                .build(1),
                        newInnovationAreaResource()
                                .withId(2L)
                                .withName("Robotics and AS")
                                .build(1),
                        newInnovationAreaResource()
                                .withId(3L)
                                .withName("Electronics, Sensors and photonics")
                                .build(1))
                .withAvailable(true, false, false)
                .withMostRecentAssessmentState(null, PENDING, OPEN)
                .withTotalApplicationsCount(0L)
                .withAssignedCount(0L)
                .withSubmittedCount(0L)
                .withSkillAreas("Solar Power, Genetics, Recycling", "Human computer interaction, Wearables, IoT", "Electronic/photonic components")
                .build(3);

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);
        when(competitionParticipantRepositoryMock.getByCompetitionIdAndRole(competition.getId(), CompetitionParticipantRole.ASSESSOR)).thenReturn(competitionParticipants);
        when(innovationAreaMapperMock.mapToResource(isA(InnovationArea.class))).then(invocation -> {
            InnovationArea argument = invocation.getArgumentAt(0, InnovationArea.class);
            return newInnovationAreaResource()
                    .withId(argument.getId())
                    .withName(argument.getName())
                    .build();
        });
        when(assessmentRepositoryMock.findFirstByParticipantUserIdAndTargetIdOrderByIdAsc(isA(Long.class), eq(application.getId()))).then(invocation ->
                ofNullable(assessmentsForUsers.get(invocation.getArgumentAt(0, Long.class))));
        // TODO replace any's
        // TODO replace scores
        when(assessmentRepositoryMock.countByParticipantUserIdAndActivityStateStateNotIn(any(), any())).thenReturn(0L);
        when(assessmentRepositoryMock.countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(any(), eq(competition.getId()), any())).thenReturn(0L);

        List<ApplicationAssessorResource> found = service.getAssessors(application.getId()).getSuccessObjectOrThrowException();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock, competitionParticipantRepositoryMock, innovationAreaMapperMock, assessmentRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(application.getId());
        inOrder.verify(competitionParticipantRepositoryMock).getByCompetitionIdAndRole(competition.getId(), CompetitionParticipantRole.ASSESSOR);
        competitionParticipants.forEach(competitionParticipant -> {
            Long userId = competitionParticipant.getUser().getId();
            inOrder.verify(assessmentRepositoryMock).findFirstByParticipantUserIdAndTargetIdOrderByIdAsc(userId, application.getId());
            competitionParticipant.getUser().getInnovationAreas().forEach(innovationArea -> {
                inOrder.verify(innovationAreaMapperMock).mapToResource(innovationArea);
            });
            // TODO replace any
            inOrder.verify(assessmentRepositoryMock).countByParticipantUserIdAndActivityStateStateNotIn(eq(userId), any());
            // TODO replace any and replace times(2)
            inOrder.verify(assessmentRepositoryMock, times(2)).countByParticipantUserIdAndTargetCompetitionIdAndActivityStateStateIn(eq(userId), eq(competition.getId()), any());
        });
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void getApplicationAssessmentSummary() throws Exception {
        Application application = newApplication()
                .withName("Progressive machines")
                .withCompetition(newCompetition()
                        .withName("Connected digital additive manufacturing")
                        .build())
                .withProcessRoles(newProcessRole()
                        .withRole(COLLABORATOR, LEADAPPLICANT, COMP_ADMIN)
                        .withOrganisation(buildOrganisationWithName("Acme Ltd."),
                                buildOrganisationWithName("IO systems"),
                                buildOrganisationWithName("Liquid Dynamics"))
                        .buildArray(3, ProcessRole.class))
                .build();

        ApplicationAssessmentSummaryResource expected = newApplicationAssessmentSummaryResource()
                .withId(application.getId())
                .withName(application.getName())
                .withCompetitionId(application.getCompetition().getId())
                .withCompetitionName(application.getCompetition().getName())
                .withPartnerOrganisations(asList("Acme Ltd.", "IO systems"))
                .build();

        when(applicationRepositoryMock.findOne(application.getId())).thenReturn(application);

        ApplicationAssessmentSummaryResource found = service.getApplicationAssessmentSummary(application.getId()).getSuccessObjectOrThrowException();

        assertEquals(expected, found);

        InOrder inOrder = inOrder(applicationRepositoryMock);
        inOrder.verify(applicationRepositoryMock).findOne(application.getId());
        inOrder.verifyNoMoreInteractions();
    }

    private Organisation buildOrganisationWithName(String name) {
        return newOrganisation()
                .withName(name)
                .build();
    }

    private ActivityState buildActivityStateWithState(AssessmentStates state) {
        return new ActivityState(APPLICATION_ASSESSMENT, state.getBackingState());
    }
}