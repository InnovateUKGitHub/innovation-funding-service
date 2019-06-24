package org.innovateuk.ifs.application.transactional;

import org.innovateuk.ifs.applicant.resource.dashboard.ApplicantDashboardResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationForEuGrantTransferResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInProgressResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardApplicationInSetupResource;
import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousApplicationResource;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.mapper.ApplicationMapper;
import org.innovateuk.ifs.application.repository.ApplicationRepository;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.transactional.CompetitionService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.transactional.UsersRolesService;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Optional;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.EU_GRANT_TRANSFER;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_PROGRESS;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.IN_SETUP;
import static org.innovateuk.ifs.applicant.resource.dashboard.DashboardSection.PREVIOUS;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.resource.ApplicationState.OPEN;
import static org.innovateuk.ifs.application.resource.ApplicationState.REJECTED;
import static org.innovateuk.ifs.application.resource.ApplicationState.SUBMITTED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionResource.H2020_TYPE_NAME;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;
import static org.innovateuk.ifs.project.resource.ProjectState.WITHDRAWN;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.Role.COLLABORATOR;
import static org.innovateuk.ifs.user.resource.Role.LEADAPPLICANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class ApplicationDashboardServiceImplTest {

    @Mock
    private ApplicationMapper applicationMapperMock;
    @Mock
    private ProjectUserRepository projectUserRepositoryMock;
    @Mock
    private ProjectMapper projectMapperMock;
    @Mock
    private InterviewAssignmentService interviewAssignmentServiceMock;
    @Mock
    private UsersRolesService usersRolesServiceMock;
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private CompetitionService competitionServiceMock;
    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private ApplicationRepository applicationRepositoryMock;
    @Mock
    private ProcessRoleRepository processRoleRepositoryMock;

    @InjectMocks
    private ApplicationDashboardServiceImpl service = new ApplicationDashboardServiceImpl();

    private static final ZonedDateTime TOMORROW = ZonedDateTime.now().plusDays(1);

    private long userId = 1L;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        setupDashboard();
    }

    @Test
    public void getInSetup() {
        ApplicantDashboardResource dashboard = service.getApplicantDashboard(userId).getSuccess();

        assertEquals(2, dashboard.getInSetup().size());

        DashboardApplicationInSetupResource projectFive = dashboard.getInSetup().get(0);
        assertEquals(14L, projectFive.getProjectId());
        assertEquals("E - In Progress", projectFive.getProjectTitle());
        assertEquals("E - In Progress", projectFive.getTitle());
        assertEquals(8L, projectFive.getApplicationId());
        assertEquals("Open Competition", projectFive.getCompetitionTitle());
        assertEquals(IN_SETUP, projectFive.getDashboardSection());

        DashboardApplicationInSetupResource projectSix = dashboard.getInSetup().get(1);
        assertEquals(14L, projectSix.getProjectId());
        assertEquals("F - Complete", projectSix.getProjectTitle());
        assertEquals("F - Complete", projectSix.getTitle());
        assertEquals(9L, projectSix.getApplicationId());
        assertEquals("Open Competition", projectSix.getCompetitionTitle());
        assertEquals(IN_SETUP, projectSix.getDashboardSection());
    }

    @Test
    public void getEuGrantTransfer() {
        ApplicantDashboardResource dashboard = service.getApplicantDashboard(userId).getSuccess();

        assertEquals(2, dashboard.getEuGrantTransfer().size());

        DashboardApplicationForEuGrantTransferResource projectTwo = dashboard.getEuGrantTransfer().get(0);
        assertEquals(0, projectTwo.getApplicationProgress());
        assertNull(projectTwo.getApplicationState());
        assertEquals(11L, (long) projectTwo.getProjectId());
        assertEquals("B - EU In Setup 1", projectTwo.getTitle());
        assertEquals(5L, projectTwo.getApplicationId());
        assertEquals("Eu Competition", projectTwo.getCompetitionTitle());
        assertEquals(EU_GRANT_TRANSFER, projectTwo.getDashboardSection());

        DashboardApplicationForEuGrantTransferResource projectFour = dashboard.getEuGrantTransfer().get(1);
        assertEquals(0, projectFour.getApplicationProgress());
        assertNull(projectFour.getApplicationState());
        assertEquals(13L, (long) projectFour.getProjectId());
        assertEquals("D - EU In Setup 2", projectFour.getTitle());
        assertEquals(7L, projectFour.getApplicationId());
        assertEquals("Eu Competition", projectFour.getCompetitionTitle());
        assertEquals(EU_GRANT_TRANSFER, projectFour.getDashboardSection());
    }

    @Test
    public void getInProgress() {
        ApplicantDashboardResource dashboard = service.getApplicantDashboard(userId).getSuccess();

        assertEquals(1, dashboard.getInProgress().size());

        DashboardApplicationInProgressResource projectFive = dashboard.getInProgress().get(0);
        assertEquals(FALSE, projectFive.isAssignedToMe());
        assertEquals(OPEN, projectFive.getApplicationState());
        assertEquals(TRUE, projectFive.isLeadApplicant());
        assertEquals(TOMORROW, projectFive.getEndDate());
        assertEquals(0, projectFive.getDaysLeft());
        assertEquals(0, projectFive.getApplicationProgress());
        assertEquals("E - In Progress", projectFive.getTitle());
        assertEquals(8L, projectFive.getApplicationId());
        assertEquals("Open Competition", projectFive.getCompetitionTitle());
        assertEquals(IN_PROGRESS, projectFive.getDashboardSection());
    }

    @Test
    public void getPrevious() {
        ApplicantDashboardResource dashboard = service.getApplicantDashboard(userId).getSuccess();

        assertEquals(1, dashboard.getPrevious().size());

        DashboardPreviousApplicationResource projectSix = dashboard.getPrevious().get(0);
        assertEquals(FALSE, projectSix.isAssignedToMe());
        assertEquals(REJECTED, projectSix.getApplicationState());
        assertEquals(FALSE, projectSix.isLeadApplicant());
        assertNull(projectSix.getEndDate());
        assertEquals(0, projectSix.getDaysLeft());
        assertEquals(0, projectSix.getApplicationProgress());
        assertEquals(FALSE, projectSix.isAssignedToInterview());
        assertEquals("F - Complete", projectSix.getTitle());
        assertEquals(9L, projectSix.getApplicationId());
        assertNull(projectSix.getCompetitionTitle());
        assertEquals(PREVIOUS, projectSix.getDashboardSection());
    }

    @Test
    public void getApplicantDashboardTotal() {
        ApplicantDashboardResource dashboard = service.getApplicantDashboard(userId).getSuccess();
        int dashboardProjectCount = dashboard.getInSetup().size() + dashboard.getEuGrantTransfer().size() + dashboard.getInProgress().size() + dashboard.getPrevious().size();

        assertEquals(6, dashboardProjectCount);
    }

    private void setupDashboard() {
        User user = newUser().withId(userId).build();

        long competitionOneId = 1L;
        long competitionTwoId = 2L;
        long competitionThreeId = 3L;

        String competitionOneTitle = "Eu Competition";
        String competitionTwoTitle = "Open Competition";
        String competitionThreeTitle = "Closed Competition";

        long applicationOneId = 4L;
        long applicationTwoId = 5L;
        long applicationThreeId = 6L;
        long applicationFourId = 7L;
        long applicationFiveId = 8L;
        long applicationSixId = 9L;

        BigDecimal applicationOneCompletion = BigDecimal.ZERO;
        BigDecimal applicationTwoCompletion = new BigDecimal(0.25);
        BigDecimal applicationThreeCompletion = new BigDecimal(0.5);
        BigDecimal applicationFourCompletion = new BigDecimal(0.75);
        BigDecimal applicationFiveCompletion = new BigDecimal(0.99);
        BigDecimal applicationSixCompletion = BigDecimal.ONE;

        String projectOneName = "A - In Setup";
        String projectTwoName = "B - EU In Setup 1";
        String projectThreeName = "C - Withdrawn";
        String projectFourName = "D - EU In Setup 2";
        String projectFiveName = "E - In Progress";
        String projectSixName = "F - Complete";

        long projectOneId = 10L;
        long projectTwoId = 11L;
        long projectThreeId = 12L;
        long projectFourId = 13L;
        long projectFiveId = 14L;
        long projectSixId = 14L;

        Project project_One = newProject().withId(projectOneId).build();
        Project project_Two = newProject().withId(projectTwoId).build();
        Project project_Three = newProject().withId(projectThreeId).build();
        Project project_Four = newProject().withId(projectFourId).build();
        Project project_Five = newProject().withId(projectFiveId).build();
        Project project_Six = newProject().withId(projectSixId).build();

        ProjectResource projectResource_One = getProjectResource(competitionOneId, applicationOneId, projectOneId, projectOneName, SETUP);
        ProjectResource projectResource_Two = getProjectResource(competitionOneId, applicationTwoId, projectTwoId, projectTwoName, LIVE);
        ProjectResource projectResource_Three = getProjectResource(competitionOneId, applicationThreeId, projectThreeId, projectThreeName, WITHDRAWN);
        ProjectResource projectResource_Four = getProjectResource(competitionOneId, applicationFourId, projectFourId, projectFourName, HANDLED_OFFLINE);
        ProjectResource projectResource_Five = getProjectResource(competitionTwoId, applicationFiveId, projectFiveId, projectFiveName, COMPLETED_OFFLINE);
        ProjectResource projectResource_Six = getProjectResource(competitionTwoId, applicationSixId, projectSixId, projectSixName, LIVE);

        ProjectUser projectUser_One = newProjectUser().withProject(project_One).withUser(user).build();
        ProjectUser projectUser_Two = newProjectUser().withProject(project_Two).withUser(user).build();
        ProjectUser projectUser_Three = newProjectUser().withProject(project_Three).withUser(user).build();
        ProjectUser projectUser_Four = newProjectUser().withProject(project_Four).withUser(user).build();
        ProjectUser projectUser_Five = newProjectUser().withProject(project_Five).withUser(user).build();
        ProjectUser projectUser_Six = newProjectUser().withProject(project_Six).withUser(user).build();

        Application application_One = newApplication().withId(applicationOneId).build();
        Application application_Two = newApplication().withId(applicationTwoId).build();
        Application application_Three = newApplication().withId(applicationThreeId).build();
        Application application_Four = newApplication().withId(applicationFourId).build();
        Application application_Five = newApplication().withApplicationState(OPEN, SUBMITTED).withId(applicationFiveId).build();
        Application application_Six = newApplication().withApplicationState(REJECTED).withId(applicationSixId).build();

        ProcessRoleResource processRoleResource_One = newProcessRoleResource().withApplication(applicationOneId).build();
        ProcessRoleResource processRoleResource_Two = newProcessRoleResource().withApplication(applicationTwoId).withRole(COLLABORATOR).build();
        ProcessRoleResource processRoleResource_Three = newProcessRoleResource().withApplication(applicationThreeId).build();
        ProcessRoleResource processRoleResource_Four = newProcessRoleResource().withApplication(applicationFourId).withRole(LEADAPPLICANT).build();
        ProcessRoleResource processRoleResource_Five = newProcessRoleResource().withApplication(applicationFiveId).withRole(LEADAPPLICANT).build();
        ProcessRoleResource processRoleResource_Six = newProcessRoleResource().withApplication(applicationSixId).withRole(LEADAPPLICANT).build();

        ProcessRole processRole_One = newProcessRole().withId(applicationOneId).withApplication(application_One).build();
        ProcessRole processRole_Two = newProcessRole().withId(applicationTwoId).withApplication(application_Two).build();
        ProcessRole processRole_Three = newProcessRole().withId(applicationThreeId).withApplication(application_Three).build();
        ProcessRole processRole_Four = newProcessRole().withId(applicationFourId).withApplication(application_Four).build();
        ProcessRole processRole_Five = newProcessRole().withId(applicationFiveId).withApplication(application_Five).build();
        ProcessRole processRole_Six = newProcessRole().withId(applicationSixId).withApplication(application_Six).build();

        CompetitionResource competitionOneResource = newCompetitionResource().withId(competitionOneId).withName(competitionOneTitle).withCompetitionType().withCompetitionTypeName(H2020_TYPE_NAME).build();
        CompetitionResource competitionTwoResource = newCompetitionResource().withId(competitionTwoId).withCompetitionStatus(CompetitionStatus.OPEN).withEndDate(TOMORROW).withName(competitionTwoTitle).build();
        CompetitionResource competitionThreeResource = newCompetitionResource().withId(competitionTwoId).withCompetitionStatus(CompetitionStatus.CLOSED).withName(competitionThreeTitle).build();

        ApplicationResource applicationResource_One = newApplicationResource().withCompetition(projectResource_One.getCompetition()).withId(applicationOneId).withCompletion(applicationOneCompletion).withName(projectOneName).build();
        ApplicationResource applicationResource_Two = newApplicationResource().withCompetition(projectResource_Two.getCompetition()).withId(applicationTwoId).withCompletion(applicationTwoCompletion).withName(projectTwoName).build();
        ApplicationResource applicationResource_Three = newApplicationResource().withCompetition(projectResource_Three.getCompetition()).withId(applicationThreeId).withCompletion(applicationThreeCompletion).withName(projectThreeName).build();
        ApplicationResource applicationResource_Four = newApplicationResource().withCompetition(projectResource_Four.getCompetition()).withId(applicationFourId).withCompletion(applicationFourCompletion).withName(projectFourName).build();
        ApplicationResource applicationResource_Five = newApplicationResource().withCompetition(projectResource_Five.getCompetition()).withId(applicationFiveId).withCompletion(applicationFiveCompletion).withName(projectFiveName).withApplicationState(OPEN).withCompetitionStatus(CompetitionStatus.OPEN).build();
        ApplicationResource applicationResource_Six = newApplicationResource().withCompetition(projectResource_Six.getCompetition()).withId(applicationSixId).withCompletion(applicationSixCompletion).withName(projectSixName).withApplicationState(REJECTED).withCompetitionStatus(CompetitionStatus.CLOSED).build();

        when(projectMapperMock.mapToResource(project_One)).thenReturn(projectResource_One);
        when(projectMapperMock.mapToResource(project_Two)).thenReturn(projectResource_Two);
        when(projectMapperMock.mapToResource(project_Three)).thenReturn(projectResource_Three);
        when(projectMapperMock.mapToResource(project_Four)).thenReturn(projectResource_Four);
        when(projectMapperMock.mapToResource(project_Five)).thenReturn(projectResource_Five);
        when(projectMapperMock.mapToResource(project_Six)).thenReturn(projectResource_Six);

        when(userRepositoryMock.findById(userId)).thenReturn(Optional.of(user));

        when(applicationRepositoryMock.findById(applicationOneId)).thenReturn(Optional.of(application_One));
        when(applicationRepositoryMock.findById(applicationTwoId)).thenReturn(Optional.of(application_Two));
        when(applicationRepositoryMock.findById(applicationThreeId)).thenReturn(Optional.of(application_Three));
        when(applicationRepositoryMock.findById(applicationFourId)).thenReturn(Optional.of(application_Four));
        when(applicationRepositoryMock.findById(applicationFiveId)).thenReturn(Optional.of(application_Five));
        when(applicationRepositoryMock.findById(applicationSixId)).thenReturn(Optional.of(application_Six));

        when(applicationMapperMock.mapToResource(application_One)).thenReturn(applicationResource_One);
        when(applicationMapperMock.mapToResource(application_Two)).thenReturn(applicationResource_Two);
        when(applicationMapperMock.mapToResource(application_Three)).thenReturn(applicationResource_Three);
        when(applicationMapperMock.mapToResource(application_Four)).thenReturn(applicationResource_Four);
        when(applicationMapperMock.mapToResource(application_Five)).thenReturn(applicationResource_Five);
        when(applicationMapperMock.mapToResource(application_Six)).thenReturn(applicationResource_Six);

        when(competitionServiceMock.getCompetitionById(competitionOneId)).thenReturn(serviceSuccess(competitionOneResource));
        when(competitionServiceMock.getCompetitionById(competitionTwoId)).thenReturn(serviceSuccess(competitionTwoResource));
        when(competitionServiceMock.getCompetitionById(competitionThreeId)).thenReturn(serviceSuccess(competitionThreeResource));

        when(interviewAssignmentServiceMock.isApplicationAssigned(applicationFiveId)).thenReturn(serviceSuccess(true));

        when(projectUserRepositoryMock.findByUserId(userId)).thenReturn(asList(projectUser_One, projectUser_Two, projectUser_Three, projectUser_Four, projectUser_Five, projectUser_Six));

        when(usersRolesServiceMock.getProcessRolesByUserId(userId)).thenReturn(serviceSuccess(asList(processRoleResource_One, processRoleResource_Two, processRoleResource_Three, processRoleResource_Four, processRoleResource_Five, processRoleResource_Six)));
        when(projectServiceMock.findByUserId(userId)).thenReturn(serviceSuccess(asList(projectResource_One, projectResource_Two, projectResource_Three, projectResource_Four, projectResource_Five, projectResource_Six)));
        when(processRoleRepositoryMock.findByUser(user)).thenReturn(asList(processRole_One, processRole_Two, processRole_Three, processRole_Four, processRole_Five, processRole_Six));
    }

    @NotNull
    private ProjectResource getProjectResource(long competitionId, long applicationId, long projectId, String projectName, ProjectState projectState) {
        ProjectResource projectResource = new ProjectResource();
        projectResource.setApplication(applicationId);
        projectResource.setCompetition(competitionId);
        projectResource.setProjectState(projectState);
        projectResource.setId(projectId);
        projectResource.setName(projectName);
        return projectResource;
    }

}