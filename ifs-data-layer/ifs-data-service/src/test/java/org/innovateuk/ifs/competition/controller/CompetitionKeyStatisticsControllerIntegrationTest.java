package org.innovateuk.ifs.competition.controller;

import org.innovateuk.ifs.BaseControllerIntegrationTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.repository.CompetitionRepository;
import org.innovateuk.ifs.competition.resource.*;
import org.innovateuk.ifs.competition.transactional.CompetitionKeyStatisticsService;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentKeyStatisticsResource;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder.newInterviewAssignment;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class CompetitionKeyStatisticsControllerIntegrationTest extends BaseControllerIntegrationTest<CompetitionKeyStatisticsController> {

    @Autowired
    private CompetitionRepository competitionRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private InterviewAssignmentRepository interviewAssignmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Override
    protected void setControllerUnderTest(CompetitionKeyStatisticsController controller) {
        this.controller = controller;
    }

    @Mock
    protected CompetitionKeyStatisticsService competitionKeyStatisticsService;

    @Before
    public void setup() {
        loginCompAdmin();
    }

    @Test
    public void getReadyToOpenKeyStatistics() {
        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = controller.getReadyToOpenKeyStatistics(1L).getSuccess();
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
    }

    @Test
    public void getOpenKeyStatistics() {
        CompetitionOpenKeyStatisticsResource keyStatisticsResource = controller.getOpenKeyStatistics(1L).getSuccess();
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
        assertEquals(2, keyStatisticsResource.getApplicationsStarted());
        assertEquals(0, keyStatisticsResource.getApplicationsPastHalf());
        assertEquals(5, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(0, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getClosedKeyStatistics() {
        CompetitionClosedKeyStatisticsResource keyStatisticsResource = controller.getClosedKeyStatistics(1L).getSuccess();
        assertEquals(0, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(0, keyStatisticsResource.getAssessorsInvited());
        assertEquals(0, keyStatisticsResource.getApplicationsRequiringAssessors());
        assertEquals(0, keyStatisticsResource.getAssessorsWithoutApplications());
        assertEquals(9, keyStatisticsResource.getAssignmentCount());
        assertEquals(0, keyStatisticsResource.getApplicationsPerAssessor());
    }

    @Test
    public void getInAssessmentKeyStatistics() {

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = controller.getInAssessmentKeyStatistics(1L).getSuccess();
        assertEquals(4, keyStatisticsResource.getAssessmentsStarted());
        assertEquals(9, keyStatisticsResource.getAssignmentCount());
        assertEquals(2, keyStatisticsResource.getAssessmentsSubmitted());
        assertEquals(1, keyStatisticsResource.getAssignmentsAccepted());
        assertEquals(1, keyStatisticsResource.getAssignmentsWaiting());
    }

    @Test
    public void getFundedKeyStatistics() {

        CompetitionFundedKeyStatisticsResource keyStatisticsResource = controller.getFundedKeyStatistics(1L).getSuccess();

        assertEquals(5, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(0, keyStatisticsResource.getApplicationsFunded());
        assertEquals(0, keyStatisticsResource.getApplicationsNotFunded());
        assertEquals(0, keyStatisticsResource.getApplicationsOnHold());
        assertEquals(0, keyStatisticsResource.getApplicationsNotifiedOfDecision());
        assertEquals(0, keyStatisticsResource.getApplicationsAwaitingDecision());
    }

    @Test
    public void getInterviewStatistics() {
        Competition competition = newCompetition()
                .with(id(null))
                .build();
        competitionRepository.save(competition);

        List<Application> applications = newApplication()
                .with(id(null))
                .withCompetition(competition)
                .withInAssessmentReviewPanel(false)
                .withActivityState(ApplicationState.SUBMITTED)
                .build(2);
        applicationRepository.save(applications);

        List<ProcessRole> processRoles = newProcessRole()
                .withId()
                .withRole(Role.ASSESSOR)
                .withApplication(applications.get(0), applications.get(1))
                .withUser(userRepository.findByEmail("felix.wilson@gmail.com").get())
                .build(2);


        InterviewAssignment interviewAssignment = newInterviewAssignment()
                .with(id(null))
                .withState(InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE)
                .withTarget(applications.get(0))
                .withParticipant(processRoles.get(0))
                .build();

        interviewAssignmentRepository.save(interviewAssignment);

        InterviewAssignmentKeyStatisticsResource keyStatisticsResource = controller.getInterviewStatistics(competition.getId()).getSuccess();

        assertEquals(2, keyStatisticsResource.getApplicationsInCompetition());
        assertEquals(1, keyStatisticsResource.getApplicationsAssigned());
    }
}