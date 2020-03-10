package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardInSetupRowResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardRowResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.fundingdecision.domain.FundingDecisionStatus;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.projectteam.domain.PendingPartnerProgress;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.competition.resource.CompetitionResource.H2020_TYPE_NAME;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationDashboardServiceImplTest {

    @InjectMocks
    private ApplicationDashboardServiceImpl applicationDashboardService;

    @Mock
    private InterviewAssignmentService interviewAssignmentService;
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
    private static final long USER_ID = 1L;
    private User user = newUser().withId(USER_ID).build();
    private ProcessRole processRole = newProcessRole().withRole(Role.LEADAPPLICANT).withUser(user).build();

    @Test
    public void getApplicantDashboard() {
        Application h2020Application = h2020Application();
        Application projectInSetupApplication = projectInSetupApplication();
        Application pendingPartnerInSetupApplication = pendingPartnerInSetupApplication();
        Application completedProjectApplication = completedProjectApplication();
        Application inProgressOpenCompApplication = inProgressOpenCompApplication();
        Application inProgressClosedCompApplication = inProgressClosedCompApplication();
        Application onHoldNotifiedApplication = onHoldNotifiedApplication();
        Application unsuccessfulNotifiedApplication = unsuccessfulNotifiedApplication();
        Application ineligibleApplication = ineligibleApplication();
        Application submittedAwaitingDecisionApplication = submittedAwaitingDecisionApplication();

        when(interviewAssignmentService.isApplicationAssigned(anyLong())).thenReturn(serviceSuccess(true));

        when(applicationRepository.findApplicationsForDashboard(USER_ID))
                .thenReturn(asList(h2020Application, projectInSetupApplication, pendingPartnerInSetupApplication, completedProjectApplication,
                        inProgressOpenCompApplication, inProgressClosedCompApplication, onHoldNotifiedApplication,
                        unsuccessfulNotifiedApplication, ineligibleApplication, submittedAwaitingDecisionApplication));

        ApplicantDashboardResource dashboardResource = applicationDashboardService.getApplicantDashboard(USER_ID).getSuccess();

        assertEquals(1, dashboardResource.getEuGrantTransfer().size());
        assertEquals(3, dashboardResource.getInProgress().size());
        assertEquals(2, dashboardResource.getInSetup().size());
        assertEquals(4, dashboardResource.getPrevious().size());

        assertListContainsApplication(h2020Application, dashboardResource.getEuGrantTransfer());
        DashboardInSetupRowResource notPendingResource = assertListContainsApplication(projectInSetupApplication, dashboardResource.getInSetup());
        DashboardInSetupRowResource pendingResource = assertListContainsApplication(pendingPartnerInSetupApplication, dashboardResource.getInSetup());
        assertListContainsApplication(completedProjectApplication, dashboardResource.getPrevious());
        assertListContainsApplication(inProgressOpenCompApplication, dashboardResource.getInProgress());
        assertListContainsApplication(inProgressClosedCompApplication, dashboardResource.getPrevious());
        assertListContainsApplication(onHoldNotifiedApplication, dashboardResource.getInProgress());
        assertListContainsApplication(unsuccessfulNotifiedApplication, dashboardResource.getPrevious());
        assertListContainsApplication(ineligibleApplication, dashboardResource.getPrevious());
        assertListContainsApplication(submittedAwaitingDecisionApplication, dashboardResource.getInProgress());

        assertTrue(pendingResource.isPendingPartner());
        assertFalse(notPendingResource.isPendingPartner());

    }

    private <R extends DashboardRowResource> R assertListContainsApplication(Application application, List<R> applications) {
        Optional<R> resource = applications.stream().filter(row -> application.getId().equals(row.getApplicationId())).findFirst();
        assertTrue(resource.isPresent());
        return resource.get();
    }

    private Application submittedAwaitingDecisionApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();
    }

    private Application ineligibleApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.INELIGIBLE_INFORMED)
                .build();
    }

    private Application unsuccessfulNotifiedApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.REJECTED)
                .withFundingDecision(FundingDecisionStatus.UNFUNDED)
                .withManageFundingEmailDate(ZonedDateTime.now())
                .build();
    }

    private Application onHoldNotifiedApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.SUBMITTED)
                .withFundingDecision(FundingDecisionStatus.ON_HOLD)
                .withManageFundingEmailDate(ZonedDateTime.now())
                .build();
    }

    private Application inProgressClosedCompApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withApplicationState(ApplicationState.OPENED)
                .withCompetition(closedCompetition)
                .build();
    }

    private Application inProgressOpenCompApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(openCompetition)
                .withApplicationState(ApplicationState.OPENED)
                .withCompetition(newCompetition().withSetupComplete(true).withStartDate(ZonedDateTime.now().minusDays(2)).withEndDate(ZonedDateTime.now().plusDays(1)).build())
                .build();
    }

    private Application completedProjectApplication() {
        return newApplication()
                .withProcessRole(processRole)
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.APPROVED)
                .withProject(newProject().withProjectProcess(newProjectProcess().withActivityState(ProjectState.LIVE).build()).build())
                .build();
    }

    private Application projectInSetupApplication() {
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.APPROVED)
                .withProject(newProject()
                        .withName("projectInSetupApplication")
                        .withProjectProcess(newProjectProcess().withActivityState(ProjectState.SETUP).build())
                        .withProjectUsers(newProjectUser().withUser(newUser().withId(USER_ID).build())
                                .withPartnerOrganisation(newPartnerOrganisation()
                                    .withOrganisation(newOrganisation().build())
                                    .build())
                                .build(1))
                        .build())
                .build();
    }

    private Application pendingPartnerInSetupApplication() {
        PendingPartnerProgress progress = new PendingPartnerProgress(null);
        return newApplication()
                .withCompetition(closedCompetition)
                .withApplicationState(ApplicationState.APPROVED)
                .withProject(newProject()
                        .withName("pendingPartnerInSetupApplication")
                        .withProjectProcess(newProjectProcess().withActivityState(ProjectState.SETUP).build())
                        .withProjectUsers(newProjectUser().withUser(newUser().withId(USER_ID).build())
                                .withPartnerOrganisation(newPartnerOrganisation()
                                        .withOrganisation(newOrganisation().build())
                                        .withPendingPartnerProgress(progress).build())
                                .build(1))
                        .build())
                .build();
    }

    private Application h2020Application() {
        return newApplication()
                .withApplicationState(ApplicationState.SUBMITTED)
                .withCompetition(newCompetition().withCompetitionType(newCompetitionType().withName(H2020_TYPE_NAME).build()).build())
                .build();
    }
}