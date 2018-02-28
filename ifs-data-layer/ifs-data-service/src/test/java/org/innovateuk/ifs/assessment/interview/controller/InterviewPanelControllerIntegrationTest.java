package org.innovateuk.ifs.assessment.interview.controller;

import com.drew.lang.Iterables;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.category.domain.InnovationArea;
import org.innovateuk.ifs.category.repository.InnovationAreaRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationListResource;
import org.innovateuk.ifs.profile.domain.Profile;
import org.innovateuk.ifs.profile.repository.ProfileRepository;
import org.innovateuk.ifs.user.domain.Organisation;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.Role;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.OrganisationRepository;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.RoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.innovateuk.ifs.workflow.resource.State;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.category.builder.InnovationAreaBuilder.newInnovationArea;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.invite.builder.StagedApplicationListResourceBuilder.newStagedApplicationListResource;
import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.profile.builder.ProfileBuilder.newProfile;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.UserRoleType.ASSESSOR;
import static org.innovateuk.ifs.user.resource.UserRoleType.LEADAPPLICANT;
import static org.junit.Assert.*;

public class InterviewPanelControllerIntegrationTest  extends BaseControllerIntegrationTest<InterviewPanelController> {

    private List<Application> applications;

    private Competition competition;

    private Pageable pageable;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private AssessmentInterviewPanelRepository assessmentInterviewPanelRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InnovationAreaRepository innovationAreaRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    @Override
    public void setControllerUnderTest(InterviewPanelController controller) {
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
                .withActivityState(activityStateRepository.findOneByActivityTypeAndState(ActivityType.APPLICATION, State.SUBMITTED))
                .build(2);
        applicationRepository.save(applications);

        User felixWilson = userRepository.findByEmail("felix.wilson@gmail.com").orElse(null);

        List<InnovationArea> innovationAreas = newInnovationArea()
                .withId()
                .withName("Metallurgy", "Alchemy", "Forgemastery")
                .build(3);

        innovationAreaRepository.save(innovationAreas);

        Profile profile = newProfile()
                .withId()
                .withBusinessType(ACADEMIC)
                .withInnovationAreas(innovationAreas)
                .build();

        profileRepository.save(profile);

        felixWilson.setProfileId(profile.getId());

        Role assessorRole = roleRepository.findOneByName(ASSESSOR.getName());

        List<ProcessRole> processRoles = newProcessRole()
                .withId()
                .withRole(assessorRole)
                .withApplication(applications.get(0), applications.get(1))
                .withUser(felixWilson, felixWilson)
                .build(2);

        User steveSmith = userRepository.findByEmail("steve.smith@empire.com").orElse(null);
        List<Organisation> organisations = newOrganisation()
                .withId()
                .withName("Test Org 1", "Test Org 2", "Test Org 3")
                .build(3);

        organisationRepository.save(organisations);

        Role leadApplicantRole = roleRepository.findOneByName(LEADAPPLICANT.getName());

        processRoles.addAll(
                newProcessRole()
                        .withRole(leadApplicantRole)
                        .withApplication(applications.get(0), applications.get(1))
                        .withUser(steveSmith)
                        .withOrganisationId(organisations.get(0).getId(), organisations.get(1).getId())
                        .build(2)
        );

        processRoleRepository.save(processRoles);
    }

    @After
    public void clearDown() {
        flushAndClearSession();
    }

    @Test
    public void assignApplication() throws Exception {

        StagedApplicationListResource stagedApplicationListResource = newStagedApplicationListResource()
                .withInvites(
                        newStagedApplicationResource()
                                .withApplicationId(applications.get(0).getId())
                                .withCompetitionId(competition.getId())
                                .build(1)
                )
                .build();

        flushAndClearSession();

        assertTrue(Iterables.toList(assessmentInterviewPanelRepository.findAll()).isEmpty());
        controller.assignApplications(stagedApplicationListResource);
        assertFalse(Iterables.toList(assessmentInterviewPanelRepository.findAll()).isEmpty());

        AssessmentInterviewPanel interviewPanel = Iterables.toList(assessmentInterviewPanelRepository.findAll()).get(0);

        assertEquals(applications.get(0).getId(), interviewPanel.getParticipant().getApplicationId());
    }

    @Test
    public void getAvailableApplications() {

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                AssessmentInterviewPanelState.CREATED.getBackingState()
        );

        AssessmentInterviewPanel interviewPanel = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState)
                .build();

        assessmentInterviewPanelRepository.save(interviewPanel);

        RestResult<AvailableApplicationPageResource> availableApplicationPageResourceRestResult = controller.getAvailableApplications(competition.getId(), pageable);
        assertTrue(availableApplicationPageResourceRestResult.isSuccess());

        AvailableApplicationPageResource availableApplicationPageResource = availableApplicationPageResourceRestResult.getSuccess();

        assertEquals(2, availableApplicationPageResource.getTotalElements());
    }

    @Test
    public void getAvailableApplicationsIds() {

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                AssessmentInterviewPanelState.CREATED.getBackingState()
        );

        AssessmentInterviewPanel interviewPanel = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState)
                .withTarget(applications.get(0))
                .build();

        assessmentInterviewPanelRepository.save(interviewPanel);

        RestResult<List<Long>> availableApplicationIds = controller.getAvailableApplicationIds(competition.getId());
        assertTrue(availableApplicationIds.isSuccess());

        List<Long> availableApplicationIdsSuccess = availableApplicationIds.getSuccess();

        assertEquals(1, availableApplicationIdsSuccess.size());
        assertEquals(applications.get(1).getId().longValue(), availableApplicationIdsSuccess.get(0).longValue());
    }

    @Test
    public void getStagedApplications() {

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                AssessmentInterviewPanelState.CREATED.getBackingState()
        );

        AssessmentInterviewPanel interviewPanel = newAssessmentInterviewPanel()
                .with(id(null))
                .withActivityState(activityState)
                .withTarget(applications.get(0))
                .build();

        assessmentInterviewPanelRepository.save(interviewPanel);

        RestResult<InterviewPanelStagedApplicationPageResource> interviewPanelStagedApplicationPageResourceRestResult = controller.getStagedApplications(competition.getId(), pageable);
        assertTrue(interviewPanelStagedApplicationPageResourceRestResult.isSuccess());

        InterviewPanelStagedApplicationPageResource interviewPanelStagedApplicationPageResource = interviewPanelStagedApplicationPageResourceRestResult.getSuccess();

        assertEquals(1, interviewPanelStagedApplicationPageResource.getTotalElements());
    }
}
