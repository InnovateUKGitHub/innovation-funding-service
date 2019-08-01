package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardRowResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.EnumSet;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionResource.H2020_TYPE_NAME;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDashboardServiceImplTest {

    @InjectMocks
    private ApplicationDashboardServiceImpl applicationDashboardService;

    @Mock
    private InterviewAssignmentService interviewAssignmentService;
    @Mock
    private QuestionStatusService questionStatusService;
    @Mock
    private ApplicationRepository applicationRepository;

    private Competition closedCompetition = newCompetition().withSetupComplete(true)
            .withStartDate(ZonedDateTime.now().minusDays(2))
            .withEndDate(ZonedDateTime.now().minusDays(1))
            .build();
    private Competition openCompetition = newCompetition().withSetupComplete(true)
            .withStartDate(ZonedDateTime.now().minusDays(2))
            .withEndDate(ZonedDateTime.now().plusDays(1))
            .build();

    @Test
    public void getApplicantDashboard() {
        long userId = 1L;
        Application h2020Application = h2020Application();
        Application projectInSetupApplication = projectInSetupApplication();
        Application completedProjectApplication = completedProjectApplication();
        Application inProgressOpenCompApplication = inProgressOpenCompApplication();
        Application inProgressClosedCompApplication = inProgressClosedCompApplication();
        Application onHoldNotifiedApplication = onHoldNotifiedApplication();
        Application unsuccessfulNotifedApplication = unsuccessfulNotifedApplication();
        Application ineligibleApplication = ineligibleApplication();
        Application submittedAwaitingDecisionApplication = submittedAwaitingDecisionApplication();

        when(interviewAssignmentService.isApplicationAssigned(anyLong())).thenReturn(serviceSuccess(true));

        when(applicationRepository.findApplicationByUserAndRole(EnumSet.of(LEADAPPLICANT, COLLABORATOR), userId))
                .thenReturn(asList(h2020Application, projectInSetupApplication, completedProjectApplication,
                        inProgressOpenCompApplication, inProgressClosedCompApplication, onHoldNotifiedApplication,
                        unsuccessfulNotifedApplication, ineligibleApplication, submittedAwaitingDecisionApplication));

        ApplicantDashboardResource dashboardResource = applicationDashboardService.getApplicantDashboard(userId).getSuccess();

        assertEquals(1, dashboardResource.getEuGrantTransfer().size());
        assertEquals(3, dashboardResource.getInProgress().size());
        assertEquals(1, dashboardResource.getInSetup().size());
        assertEquals(4, dashboardResource.getPrevious().size());

        assertListContainsApplication(h2020Application, dashboardResource.getEuGrantTransfer());
        assertListContainsApplication(projectInSetupApplication, dashboardResource.getInSetup());
        assertListContainsApplication(completedProjectApplication, dashboardResource.getPrevious());
        assertListContainsApplication(inProgressOpenCompApplication, dashboardResource.getInProgress());
        assertListContainsApplication(inProgressClosedCompApplication, dashboardResource.getPrevious());
        assertListContainsApplication(onHoldNotifiedApplication, dashboardResource.getInProgress());
        assertListContainsApplication(unsuccessfulNotifedApplication, dashboardResource.getPrevious());
        assertListContainsApplication(ineligibleApplication, dashboardResource.getPrevious());
        assertListContainsApplication(submittedAwaitingDecisionApplication, dashboardResource.getInProgress());
    }

    private void assertListContainsApplication(Application application, List<? extends DashboardRowResource> applications) {
        assertTrue(applications.stream().anyMatch(row -> application.getId().equals(row.getApplicationId())));
    }

    private Application submittedAwaitingDecisionApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();
    }

    private Application ineligibleApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.INELIGIBLE_INFORMED)
                .build();
    }

    private Application unsuccessfulNotifedApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.REJECTED)
                .withFundingDecision(FundingDecisionStatus.UNFUNDED)
                .withManageFundingEmailDate(ZonedDateTime.now())
                .build();
    }

    private Application onHoldNotifiedApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withFundingDecision(FundingDecisionStatus.ON_HOLD)
                .withManageFundingEmailDate(ZonedDateTime.now())
                .build();
    }

    private Application inProgressClosedCompApplication() {
        return newApplication()
                .withApplicationState(ApplicationState.OPENED)
                .withCompetition(closedCompetition)
                .build();
    }

    private Application inProgressOpenCompApplication() {
        return newApplication()
                .withCompetition(openCompetition)
                .withApplicationState(ApplicationState.OPENED)
                .withCompetition(newCompetition().withSetupComplete(true).withStartDate(ZonedDateTime.now().minusDays(2)).withEndDate(ZonedDateTime.now().plusDays(1)).build())
                .build();
    }

    private Application completedProjectApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.APPROVED)
                .withProject(newProject().withProjectProcess(newProjectProcess().withActivityState(ProjectState.LIVE).build()).build())
                .build();
    }

    private Application projectInSetupApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.APPROVED)
                .withProject(newProject().withProjectProcess(newProjectProcess().withActivityState(ProjectState.SETUP).build()).build())
                .build();
    }

    private Application h2020Application() {
        return newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(newCompetition().withCompetitionType(newCompetitionType().withName(H2020_TYPE_NAME).build()).build())
                .build();
    }
}