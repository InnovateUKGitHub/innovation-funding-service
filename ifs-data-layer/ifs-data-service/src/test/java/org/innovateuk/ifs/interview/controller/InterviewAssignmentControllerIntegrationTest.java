package org.innovateuk.ifs.interview.controller;

import com.drew.lang.Iterables;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.invite.resource.*;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.organisation.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.CREATED;
import static org.innovateuk.ifs.invite.builder.StagedApplicationListResourceBuilder.newStagedApplicationListResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.junit.Assert.*;

public class InterviewAssignmentControllerIntegrationTest extends BaseControllerIntegrationTest<InterviewAssignmentController> {

    private List<Application> applications;

    private List<ProcessRole> processRoles;

    private Competition competition;

    private Pageable pageable;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    @Override
    public void setControllerUnderTest(InterviewAssignmentController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() {
        loginCompAdmin();
        Organisation leadOrganisation = newOrganisation().withName("lead org").build();

        pageable = new PageRequest(1, 20);

        competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(false)
                .withActivityState(ApplicationState.SUBMITTED)
                .build(2);
        applicationRepository.saveAll(applications);

        User felixWilson = userRepository.findByEmail("felix.wilson@gmail.com").orElse(null);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withId()
                .withName("Metallurgy", "Alchemy", "Forgemastery")
                .build(3);

        innovationAreaRepository.saveAll(innovationAreas);

        Profile profile = newProfile()
                .withId()
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(innovationAreas)
                .build();

        profileRepository.save(profile);

        felixWilson.setProfileId(profile.getId());

        processRoles = newProcessRole()
                .withId()
                .withRole(Role.ASSESSOR)
                .withApplication(applications.get(0), applications.get(1))
                .withUser(felixWilson, felixWilson)
                .build(2);

        User steveSmith = userRepository.findByEmail("steve.smith@empire.com").orElse(null);
        List<Organisation> organisations = newOrganisation()
                .withId()
                .withName("Test Org 1", "Test Org 2", "Test Org 3")
                .build(3);

        organisationRepository.saveAll(organisations);

        processRoles.addAll(
                newProcessRole()
                        .withRole(Role.LEADAPPLICANT)
                        .withApplication(applications.get(0), applications.get(1))
                        .withUser(steveSmith)
                        .withOrganisationId(organisations.get(0).getId(), organisations.get(1).getId())
                        .build(2)
        );

        processRoleRepository.saveAll(processRoles);
    }

    @After
    public void clearDown() {
        flushAndClearSession();
    }

    @Test
    public void assignApplication() {
        StagedApplicationListResource stagedApplicationListResource = newStagedApplicationListResource()
                .withInvites(
                        newStagedApplicationResource()
                                .withApplicationId(applications.get(0).getId())
                                .withCompetitionId(competition.getId())
                                .build(1)
                )
                .build();

        flushAndClearSession();

        assertTrue(Iterables.toList(interviewAssignmentRepository.findAll()).isEmpty());
        controller.assignApplications(stagedApplicationListResource);
        assertFalse(Iterables.toList(interviewAssignmentRepository.findAll()).isEmpty());

        InterviewAssignment interviewAssignment = Iterables.toList(interviewAssignmentRepository.findAll()).get(0);

        assertEquals(applications.get(0).getId(), interviewAssignment.getParticipant().getApplicationId());
    }

    @Test
    public void getAvailableApplications() {
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<AvailableApplicationPageResource> availableApplicationPageResourceRestResult = controller.getAvailableApplications(competition.getId(), pageable);
        assertTrue(availableApplicationPageResourceRestResult.isSuccess());

        AvailableApplicationPageResource availableApplicationPageResource = availableApplicationPageResourceRestResult.getSuccess();

        assertEquals(2, availableApplicationPageResource.getTotalElements());
    }

    @Test
    public void getAvailableApplicationsIds() {
        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewAssignment);

        RestResult<List<Long>> availableApplicationIds = controller.getAvailableApplicationIds(competition.getId());
        assertTrue(availableApplicationIds.isSuccess());

        List<Long> availableApplicationIdsSuccess = availableApplicationIds.getSuccess();

        assertEquals(1, availableApplicationIdsSuccess.size());
        assertEquals(applications.get(1).getId().longValue(), availableApplicationIdsSuccess.get(0).longValue());
    }

    @Test
    public void getStagedApplications() {
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<InterviewAssignmentStagedApplicationPageResource> interviewPanelStagedApplicationPageResourceRestResult = controller.getStagedApplications(competition.getId(), pageable);
        assertTrue(interviewPanelStagedApplicationPageResourceRestResult.isSuccess());

        InterviewAssignmentStagedApplicationPageResource interviewAssignmentStagedApplicationPageResource = interviewPanelStagedApplicationPageResourceRestResult.getSuccess();

        assertEquals(1, interviewAssignmentStagedApplicationPageResource.getTotalElements());
    }

    @Test
    public void unstageApplication() {
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(applications.get(0))
                .withParticipant(processRoles.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<Void> result = controller.unstageApplication(applications.get(0).getId());
        assertTrue(result.isSuccess());

        InterviewAssignment interview = interviewAssignmentRepository.findOneByTargetId(applications.get(0).getId());
        assertThat(interview, is(nullValue()));
    }

    @Test
    public void getAssignedApplications() {
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(AWAITING_FEEDBACK_RESPONSE)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<InterviewAssignmentApplicationPageResource> interviewPanelApplicationPageResourceRestResult = controller.getAssignedApplications(competition.getId(), pageable);
        assertTrue(interviewPanelApplicationPageResourceRestResult.isSuccess());

        InterviewAssignmentApplicationPageResource interviewAssignmentApplicationPageResource = interviewPanelApplicationPageResourceRestResult.getSuccess();

        assertEquals(1, interviewAssignmentApplicationPageResource.getTotalElements());
    }

    @Test
    public void sendInvites() {
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(applications.get(0))
                .withParticipant(processRoles.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        AssessorInviteSendResource sendResource = new AssessorInviteSendResource("Subject", "Content");

        RestResult<Void> result = controller.sendInvites(competition.getId(), sendResource);
        assertTrue(result.isSuccess());

        List<InterviewAssignment> created = interviewAssignmentRepository.findByTargetCompetitionIdAndActivityState(competition.getId(), CREATED);
        List<InterviewAssignment> awaitingFeedback = interviewAssignmentRepository.findByTargetCompetitionIdAndActivityState(competition.getId(), InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE);

        assertEquals(created.size(), 0);
        assertEquals(awaitingFeedback.size(), 1);
    }

    @Test
    public void unstageApplications() {
        List<InterviewAssignment> interviewPanels = newInterviewAssignment()
                .with(id(null))
                .withState(CREATED)
                .withTarget(applications.get(0), applications.get(1))
                .build(2);

        interviewAssignmentRepository.saveAll(interviewPanels);

        RestResult<Void> result = controller.unstageApplications(applications.get(0).getCompetition().getId());
        assertTrue(result.isSuccess());

        InterviewAssignment interview1 = interviewAssignmentRepository.findOneByTargetId(applications.get(0).getId());
        assertThat(interview1, is(nullValue()));

        InterviewAssignment interview2 = interviewAssignmentRepository.findOneByTargetId(applications.get(1).getId());
        assertThat(interview2, is(nullValue()));
    }

    @Test
    public void isApplicationAssigned() {
        loginSteveSmith();
        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withState(AWAITING_FEEDBACK_RESPONSE)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<Boolean> result = controller.isApplicationAssigned(applications.get(0).getId());
        assertTrue(result.isSuccess());
        assertTrue(result.getSuccess());

        RestResult<Boolean> notFound = controller.isApplicationAssigned(99L);
        assertTrue(notFound.isSuccess());
        assertFalse(notFound.getSuccess());
    }
}