package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignmentResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoringofficer.domain.LegacyMonitoringOfficer;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapArray;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class MonitoringOfficerServiceImplTest extends BaseServiceUnitTest<MonitoringOfficerServiceImpl> {

    @Mock
    private MonitoringOfficerRepository projectMonitoringOfficerRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganisationService organisationServiceMock;

    @Mock
    private MonitoringOfficerInviteServiceImpl monitoringOfficerInviteServiceMock;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private LegacyMonitoringOfficerService legacyMonitoringOfficerService;

    @Test
    public void findAll() {
        List<User> moUsers = newUser().withFirstName("John", "Jane").withLastName("Doe").build(2);
        when(userRepositoryMock.findByRoles(Role.MONITORING_OFFICER)).thenReturn(moUsers);
        when(projectRepositoryMock.findAssigned(anyLong())).thenReturn(emptyList());
        when(projectRepositoryMock.findAssignable()).thenReturn(emptyList());

        List<MonitoringOfficerAssignmentResource> result = service.findAll().getSuccess();

        assertThat(result.size() == 2);
        assertThat(result.get(0).getFirstName().equals("John"));
        assertThat(result.get(0).getLastName().equals("Doe"));
        assertThat(result.get(0).getFirstName().equals("Jane"));
        assertThat(result.get(0).getLastName().equals("Doe"));

        verify(userRepositoryMock).findByRoles(Role.MONITORING_OFFICER);
        verify(projectRepositoryMock, times(2)).findAssigned(anyLong());
        verify(projectRepositoryMock, times(2)).findAssignable();
    }

    @Test
    public void getProjectMonitoringOfficer() {
        int assignedProjectsSize = 2;
        int unassignedProjectsSize = 2;

        String [] assignedOrganisationNames = {"foo", "bar"};
        User moUser = newUser().withFirstName("Tom").withLastName("Baldwin").build();
        List<Organisation> assignedProjectOrganisations = newOrganisation().withName(assignedOrganisationNames).build(assignedProjectsSize);
        List<OrganisationResource> assignProjectOrganisationResources = newOrganisationResource().withName(assignedOrganisationNames).build(assignedProjectsSize);
        List<Project> assignedProjects = newProject()
                .withApplication(newApplication()
                        .withProcessRole(
                                newProcessRole()
                                        .withRole(Role.LEADAPPLICANT)
                                        .withOrganisationId(
                                                simpleMapArray(assignedProjectOrganisations.toArray(new Organisation[0]), Organisation::getId, Long.class))
                                        .buildArray(assignedProjectsSize, ProcessRole.class)
                        )
                        .withCompetition(newCompetition().buildArray(assignedProjectsSize, Competition.class))
                        .buildArray(assignedProjectsSize, Application.class)
                )
                .withName("one", "two", "three")
                .build(assignedProjectsSize);
        List<Project> unassignedProjects = newProject()
                .withApplication(newApplication()
                        .buildArray(unassignedProjectsSize, Application.class)
                )
                .withName("four", "five")
                .build(unassignedProjectsSize);

        when(userRepositoryMock.findByIdAndRoles(moUser.getId(), Role.MONITORING_OFFICER)).thenReturn(Optional.of(moUser));
        when(projectRepositoryMock.findAssigned(moUser.getId())).thenReturn(assignedProjects);
        when(organisationServiceMock.findById(assignedProjects.get(0).getApplication().getLeadOrganisationId()))
                .thenReturn(serviceSuccess(assignProjectOrganisationResources.get(0)));
        when(organisationServiceMock.findById(assignedProjects.get(1).getApplication().getLeadOrganisationId()))
                .thenReturn(serviceSuccess(assignProjectOrganisationResources.get(1)));
        when(projectRepositoryMock.findAssignable()).thenReturn(unassignedProjects);

        MonitoringOfficerAssignmentResource projectMonitoringOfficer = service.getProjectMonitoringOfficer(moUser.getId()).getSuccess();

        assertEquals(moUser.getFirstName(), projectMonitoringOfficer.getFirstName());
        assertEquals(moUser.getLastName(), projectMonitoringOfficer.getLastName());

        for (int i=0; i < assignedProjectsSize; i++) {
            Project assignedProject = assignedProjects.get(i);
            Organisation assignedProjectOrganisation = assignedProjectOrganisations.get(i);
            MonitoringOfficerAssignedProjectResource assignedProjectResource = projectMonitoringOfficer.getAssignedProjects().get(i);
            assertEquals((long) assignedProject.getId(), assignedProjectResource.getProjectId());
            assertEquals(assignedProject.getName(), assignedProjectResource.getProjectName());
            assertEquals((long) assignedProject.getApplication().getId(), assignedProjectResource.getApplicationId());
            assertEquals((long) assignedProject.getApplication().getCompetition().getId(), assignedProjectResource.getCompetitionId());
            assertEquals(assignedProjectOrganisation.getName(), assignedProjectResource.getLeadOrganisationName());
        }

        for (int i=0; i < unassignedProjectsSize; i++) {
            Project unassignedProject = unassignedProjects.get(i);
            MonitoringOfficerUnassignedProjectResource unassignedProjectResource = projectMonitoringOfficer.getUnassignedProjects().get(i);
            assertEquals((long) unassignedProject.getId(), unassignedProjectResource.getProjectId());
            assertEquals((long) unassignedProject.getApplication().getId(), unassignedProjectResource.getApplicationId());
            assertEquals(unassignedProject.getName(), unassignedProjectResource.getProjectName());
        }

        InOrder inOrder = inOrder(userRepositoryMock, projectRepositoryMock, organisationServiceMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(moUser.getId(), Role.MONITORING_OFFICER);
        inOrder.verify(projectRepositoryMock).findAssigned(moUser.getId());
        inOrder.verify(organisationServiceMock).findById(assignedProjects.get(0).getApplication().getLeadOrganisationId());
        inOrder.verify(organisationServiceMock).findById(assignedProjects.get(1).getApplication().getLeadOrganisationId());
        inOrder.verify(projectRepositoryMock).findAssignable();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void assignProjectToMonitoringOfficer() {
        User moUser = newUser().build();
        Project project = newProject().build();

        when(userRepositoryMock.findByIdAndRoles(moUser.getId(), Role.MONITORING_OFFICER)).thenReturn(Optional.of(moUser));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(monitoringOfficerInviteServiceMock.inviteMonitoringOfficer(moUser, project)).thenReturn(serviceSuccess());

        service.assignProjectToMonitoringOfficer(moUser.getId(), project.getId()).getSuccess();

        InOrder inOrder = inOrder(userRepositoryMock, projectRepositoryMock, monitoringOfficerInviteServiceMock, projectMonitoringOfficerRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(moUser.getId(), Role.MONITORING_OFFICER);
        inOrder.verify(projectRepositoryMock).findById(project.getId());
        inOrder.verify(monitoringOfficerInviteServiceMock).inviteMonitoringOfficer(moUser, project);
        inOrder.verify(projectMonitoringOfficerRepositoryMock).save(new MonitoringOfficer(moUser, project));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void unassignProjectFromMonitoringOfficer() {
        User moUser = newUser().build();
        Project project = newProject().build();

        service.unassignProjectFromMonitoringOfficer(moUser.getId(), project.getId());

        verify(projectMonitoringOfficerRepositoryMock, only()).deleteByUserIdAndProjectId(moUser.getId(), project.getId());
    }

    @Test
    public void getMonitoringOfficerProjects() {
        long userId = 1L;
        when(projectMonitoringOfficerRepositoryMock.findByUserId(userId)).thenReturn(newMonitoringOfficer()
                .withProject(newProject().build())
                .build(1));
        when(projectMapper.mapToResource(any(Project.class))).thenReturn(newProjectResource().build());

        ServiceResult<List<ProjectResource>> result = service.getMonitoringOfficerProjects(userId);

        assertTrue(result.isSuccess());
        assertEquals(result.getSuccess().size(), 1);
    }

    @Test
    public void findMonitoringOfficerForProject_monitoringOfficerFound() {
        long projectId = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "blah@example.com";
        String phoneNumber = "012345678";

        MonitoringOfficer monitoringOfficer = newMonitoringOfficer()
                .withUser(newUser()
                        .withFirstName(firstName)
                        .withLastName(lastName)
                        .withEmailAddress(email)
                        .withPhoneNumber(phoneNumber)
                        .build()
                )
                .build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(newProject().withProjectMonitoringOfficer(monitoringOfficer).build()));

        ServiceResult<MonitoringOfficerResource> result = service.findMonitoringOfficerForProject(projectId);

        MonitoringOfficerResource resource = result.getSuccess();

        assertEquals(resource.getEmail(), email);
        assertEquals(resource.getFirstName(), firstName);
        assertEquals(resource.getLastName(), lastName);
        assertEquals(resource.getPhoneNumber(), phoneNumber);
        assertEquals(resource.getId(), monitoringOfficer.getId());
        assertEquals(resource.getProject(), (Long) projectId);
    }

    @Test
    public void findMonitoringOfficerForProject_legacyOfficerFound() {
        long projectId = 1L;
        String firstName = "firstName";
        String lastName = "lastName";
        String email = "blah@example.com";
        String phoneNumber = "012345678";

        MonitoringOfficer monitoringOfficer = newMonitoringOfficer().build();

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(newProject().withProjectMonitoringOfficer(null).build()));
        when(legacyMonitoringOfficerService.getMonitoringOfficer(projectId)).thenReturn(serviceSuccess(newLegacyMonitoringOfficerResource()
                .withEmail(email)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withPhoneNumber(phoneNumber)
                .withProject(projectId)
                .build()));

        ServiceResult<MonitoringOfficerResource> result = service.findMonitoringOfficerForProject(projectId);

        MonitoringOfficerResource resource = result.getSuccess();

        assertEquals(resource.getEmail(), email);
        assertEquals(resource.getFirstName(), firstName);
        assertEquals(resource.getLastName(), lastName);
        assertEquals(resource.getPhoneNumber(), phoneNumber);
        assertEquals(resource.getProject(), (Long) projectId);
    }

    @Test
    public void findMonitoringOfficerForProject_notFound() {
        long projectId = 1L;

        when(projectRepositoryMock.findById(projectId)).thenReturn(Optional.ofNullable(newProject().withProjectMonitoringOfficer(null).build()));
        when(legacyMonitoringOfficerService.getMonitoringOfficer(projectId)).thenReturn(serviceFailure(CommonErrors.notFoundError(LegacyMonitoringOfficer.class)));

        ServiceResult<MonitoringOfficerResource> result = service.findMonitoringOfficerForProject(projectId);

        assertTrue(result.isFailure());
    }

    @Override
    protected MonitoringOfficerServiceImpl supplyServiceUnderTest() {
        return new MonitoringOfficerServiceImpl(projectMonitoringOfficerRepositoryMock, projectRepositoryMock,
                userRepositoryMock, organisationServiceMock, projectMapper, monitoringOfficerInviteServiceMock, legacyMonitoringOfficerService);
    }
}