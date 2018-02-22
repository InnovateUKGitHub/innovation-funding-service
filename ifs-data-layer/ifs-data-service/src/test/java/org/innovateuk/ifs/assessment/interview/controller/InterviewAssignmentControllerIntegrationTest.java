package org.innovateuk.ifs.assessment.interview.controller;

import com.drew.lang.Iterables;
import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.interview.controller.InterviewAssignmentController;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteListResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
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
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteListResourceBuilder.newExistingUserStagedInviteListResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.junit.Assert.*;

public class InterviewAssignmentControllerIntegrationTest extends BaseControllerIntegrationTest<InterviewAssignmentController> {

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
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    @Override
    public void setControllerUnderTest(InterviewAssignmentController controller) {
        this.controller = controller;
    }

    @Before
    public void setUp() {
        loginCompAdmin();

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
    }

    @After
    public void clearDown() {
        flushAndClearSession();
    }

    @Test
    public void assignApplication() throws Exception {

        ExistingUserStagedInviteListResource existingUserStagedInviteListResource = newExistingUserStagedInviteListResource()
                .withInvites(
                        newExistingUserStagedInviteResource()
                                .withUserId(getPaulPlum().getId())
                                .withCompetitionId(competition.getId())
                                .build(1)
                )
                .build();

        flushAndClearSession();

        assertTrue(Iterables.toList(interviewAssignmentRepository.findAll()).isEmpty());
        controller.assignApplications(existingUserStagedInviteListResource);
        assertFalse(Iterables.toList(interviewAssignmentRepository.findAll()).isEmpty());

        InterviewAssignment interviewAssignment = Iterables.toList(interviewAssignmentRepository.findAll()).get(0);

        assertEquals(getPaulPlum().getId(), interviewAssignment.getParticipant().getApplicationId());
    }

    @Test
    public void getAvailableApplications() {

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                InterviewAssignmentState.CREATED.getBackingState()
        );

        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState)
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<AvailableApplicationPageResource> availableApplicationPageResourceRestResult = controller.getAvailableApplications(competition.getId(), pageable);
        assertTrue(availableApplicationPageResourceRestResult.isSuccess());

        AvailableApplicationPageResource availableApplicationPageResource = availableApplicationPageResourceRestResult.getSuccess();

        assertEquals(2, availableApplicationPageResource.getTotalElements());
    }

    @Test
    public void getAvailableApplicationsIds() {

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                InterviewAssignmentState.CREATED.getBackingState()
        );

        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState)
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

        ActivityState activityState = activityStateRepository.findOneByActivityTypeAndState(
                ActivityType.ASSESSMENT_INTERVIEW_PANEL,
                InterviewAssignmentState.CREATED.getBackingState()
        );

        InterviewAssignment interviewPanel = newInterviewAssignment()
                .with(id(null))
                .withActivityState(activityState)
                .withTarget(applications.get(0))
                .build();

        interviewAssignmentRepository.save(interviewPanel);

        RestResult<InterviewPanelStagedApplicationPageResource> interviewPanelStagedApplicationPageResourceRestResult = controller.getStagedApplications(competition.getId(), pageable);
        assertTrue(interviewPanelStagedApplicationPageResourceRestResult.isSuccess());

        InterviewPanelStagedApplicationPageResource interviewPanelStagedApplicationPageResource = interviewPanelStagedApplicationPageResourceRestResult.getSuccess();

        assertEquals(1, interviewPanelStagedApplicationPageResource.getTotalElements());
    }
}
