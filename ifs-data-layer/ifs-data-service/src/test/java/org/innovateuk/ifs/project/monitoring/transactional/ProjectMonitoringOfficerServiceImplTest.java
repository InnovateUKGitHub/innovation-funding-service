package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.organisation.transactional.OrganisationService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.ProjectMonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.ProjectMonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerAssignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.MonitoringOfficerUnassignedProjectResource;
import org.innovateuk.ifs.project.monitoring.resource.ProjectMonitoringOfficerResource;
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
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleMapArray;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ProjectMonitoringOfficerServiceImplTest extends BaseServiceUnitTest<ProjectMonitoringOfficerServiceImpl> {

    @Mock
    private ProjectMonitoringOfficerRepository projectMonitoringOfficerRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private OrganisationService organisationServiceMock;

    @Test
    public void findAll() {
        List<User> moUsers = newUser().withFirstName("John", "Jane").withLastName("Doe").build(2);
        when(userRepositoryMock.findByRoles(Role.MONITORING_OFFICER)).thenReturn(moUsers);
        when(projectRepositoryMock.findAssigned(anyLong())).thenReturn(emptyList());
        when(projectRepositoryMock.findAssignable()).thenReturn(emptyList());

        List<ProjectMonitoringOfficerResource> result = service.findAll().getSuccess();

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

        ProjectMonitoringOfficerResource projectMonitoringOfficer = service.getProjectMonitoringOfficer(moUser.getId()).getSuccess();

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

        service.assignProjectToMonitoringOfficer(moUser.getId(), project.getId()).getSuccess();

        InOrder inOrder = inOrder(userRepositoryMock, projectRepositoryMock, projectMonitoringOfficerRepositoryMock);
        inOrder.verify(userRepositoryMock).findByIdAndRoles(moUser.getId(), Role.MONITORING_OFFICER);
        inOrder.verify(projectRepositoryMock).findById(project.getId());
        inOrder.verify(projectMonitoringOfficerRepositoryMock).save(new ProjectMonitoringOfficer(moUser, project));
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void unassignProjectFromMonitoringOfficer() {
        User moUser = newUser().build();
        Project project = newProject().build();

        service.unassignProjectFromMonitoringOfficer(moUser.getId(), project.getId());

        verify(projectMonitoringOfficerRepositoryMock, only()).deleteByUserIdAndProjectId(moUser.getId(), project.getId());
    }

    @Override
    protected ProjectMonitoringOfficerServiceImpl supplyServiceUnderTest() {
        return new ProjectMonitoringOfficerServiceImpl(projectMonitoringOfficerRepositoryMock, projectRepositoryMock,
                userRepositoryMock, organisationServiceMock);
    }
}