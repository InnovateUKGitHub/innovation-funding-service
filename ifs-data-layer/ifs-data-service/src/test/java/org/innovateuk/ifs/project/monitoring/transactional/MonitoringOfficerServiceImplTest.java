package org.innovateuk.ifs.project.monitoring.transactional;

import org.innovateuk.ifs.commons.error.CommonErrors;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
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
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.SimpleUserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.LegacyMonitoringOfficerResourceBuilder.newLegacyMonitoringOfficerResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.user.resource.UserStatus.ACTIVE;
import static org.innovateuk.ifs.user.resource.UserStatus.PENDING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerServiceImplTest {

    @InjectMocks
    private MonitoringOfficerServiceImpl service;

    @Mock
    private MonitoringOfficerRepository projectMonitoringOfficerRepositoryMock;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private MonitoringOfficerInviteServiceImpl monitoringOfficerInviteServiceMock;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private LegacyMonitoringOfficerService legacyMonitoringOfficerService;

    @Mock
    private UserMapper userMapper;

    @Test
    public void findAll() {
        User user = newUser().build();
        when(userRepositoryMock.findDistinctByRolesInAndStatusIn(
                EnumSet.of(Role.MONITORING_OFFICER, Role.KNOWLEDGE_TRANSFER_ADVISER), EnumSet.of(PENDING, ACTIVE)))
                .thenReturn(singletonList(user));

        List<SimpleUserResource> result = service.findAll().getSuccess();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), (long) user.getId());
    }

    @Test
    public void findAllKtp() {
        User user = newUser().build();
        when(userRepositoryMock.findDistinctByRolesInAndStatusIn(
                EnumSet.of(Role.KNOWLEDGE_TRANSFER_ADVISER), EnumSet.of(PENDING, ACTIVE)))
                .thenReturn(singletonList(user));

        List<SimpleUserResource> result = service.findAllKtp().getSuccess();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), (long) user.getId());
    }

    @Test
    public void findAllNonKtp() {
        User user = newUser().build();
        when(userRepositoryMock.findDistinctByRolesInAndStatusIn(
                EnumSet.of(Role.MONITORING_OFFICER), EnumSet.of(PENDING, ACTIVE)))
                .thenReturn(singletonList(user));

        List<SimpleUserResource> result = service.findAllNonKtp().getSuccess();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0).getId(), (long) user.getId());
    }


    @Test
    public void getProjectMonitoringOfficerAsKTAAndMo() {
        User user = newUser().withFirstName("Tom").withLastName("Baldwin")
                .withRoles(newHashSet(Role.MONITORING_OFFICER, Role.KNOWLEDGE_TRANSFER_ADVISER)).build();
        MonitoringOfficerAssignedProjectResource assigned =
                new MonitoringOfficerAssignedProjectResource(1L, 1L, 1L, "assigned", "LeadyMcLeadFace");
        MonitoringOfficerUnassignedProjectResource unassigned = new MonitoringOfficerUnassignedProjectResource(2L, 2L, "unassigned");

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectMonitoringOfficerRepositoryMock.findAllAssignedProjects(user.getId())).thenReturn(singletonList(assigned));
        when(projectMonitoringOfficerRepositoryMock.findAllUnassignedProjects()).thenReturn(singletonList(unassigned));

        MonitoringOfficerAssignmentResource projectMonitoringOfficer = service.getProjectMonitoringOfficer(user.getId()).getSuccess();

        assertEquals((long) user.getId(), projectMonitoringOfficer.getUserId());
        assertEquals(user.getFirstName(), projectMonitoringOfficer.getFirstName());
        assertEquals(user.getLastName(), projectMonitoringOfficer.getLastName());

        assertEquals(assigned, projectMonitoringOfficer.getAssignedProjects().get(0));
        assertEquals(unassigned, projectMonitoringOfficer.getUnassignedProjects().get(0));
    }

    @Test
    public void getProjectMonitoringOfficerAsKTA() {
        User user = newUser().withFirstName("Tom").withLastName("Baldwin")
                .withRoles(singleton(Role.KNOWLEDGE_TRANSFER_ADVISER)).build();
        MonitoringOfficerAssignedProjectResource assigned =
                new MonitoringOfficerAssignedProjectResource(1L, 1L, 1L, "assigned", "LeadyMcLeadFace");
        MonitoringOfficerUnassignedProjectResource unassigned = new MonitoringOfficerUnassignedProjectResource(2L, 2L, "unassigned");

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectMonitoringOfficerRepositoryMock.findAssignedKTPProjects(user.getId())).thenReturn(singletonList(assigned));
        when(projectMonitoringOfficerRepositoryMock.findUnassignedKTPProjects()).thenReturn(singletonList(unassigned));

        MonitoringOfficerAssignmentResource projectMonitoringOfficer = service.getProjectMonitoringOfficer(user.getId()).getSuccess();

        assertEquals((long) user.getId(), projectMonitoringOfficer.getUserId());
        assertEquals(user.getFirstName(), projectMonitoringOfficer.getFirstName());
        assertEquals(user.getLastName(), projectMonitoringOfficer.getLastName());

        assertEquals(assigned, projectMonitoringOfficer.getAssignedProjects().get(0));
        assertEquals(unassigned, projectMonitoringOfficer.getUnassignedProjects().get(0));
    }

    @Test
    public void getProjectMonitoringOfficer() {
        User user = newUser().withFirstName("Tom").withLastName("Baldwin")
                .withRoles(singleton(Role.MONITORING_OFFICER))
                .build();
        MonitoringOfficerAssignedProjectResource assigned =
                new MonitoringOfficerAssignedProjectResource(1L, 1L, 1L, "assigned", "LeadyMcLeadFace");
        MonitoringOfficerUnassignedProjectResource unassigned = new MonitoringOfficerUnassignedProjectResource(2L, 2L, "unassigned");

        when(userRepositoryMock.findById(user.getId())).thenReturn(Optional.of(user));
        when(projectMonitoringOfficerRepositoryMock.findAssignedNonKTPProjects(user.getId())).thenReturn(singletonList(assigned));
        when(projectMonitoringOfficerRepositoryMock.findUnassignedNonKTPProjects()).thenReturn(singletonList(unassigned));

        MonitoringOfficerAssignmentResource projectMonitoringOfficer = service.getProjectMonitoringOfficer(user.getId()).getSuccess();

        assertEquals((long) user.getId(), projectMonitoringOfficer.getUserId());
        assertEquals(user.getFirstName(), projectMonitoringOfficer.getFirstName());
        assertEquals(user.getLastName(), projectMonitoringOfficer.getLastName());

        assertEquals(assigned, projectMonitoringOfficer.getAssignedProjects().get(0));
        assertEquals(unassigned, projectMonitoringOfficer.getUnassignedProjects().get(0));
    }

    @Test
    public void assignProjectToMonitoringOfficer() {
        User moUser = newUser().withRoles(singleton(Role.MONITORING_OFFICER)).build();
        Project project = newProject().build();

        when(userRepositoryMock.findById(moUser.getId())).thenReturn(Optional.of(moUser));
        when(projectRepositoryMock.findById(project.getId())).thenReturn(Optional.of(project));
        when(monitoringOfficerInviteServiceMock.inviteMonitoringOfficer(moUser, project)).thenReturn(serviceSuccess());

        service.assignProjectToMonitoringOfficer(moUser.getId(), project.getId()).getSuccess();

        InOrder inOrder = inOrder(userRepositoryMock, projectRepositoryMock, monitoringOfficerInviteServiceMock, projectMonitoringOfficerRepositoryMock);
        inOrder.verify(userRepositoryMock).findById(moUser.getId());
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

        when(projectMonitoringOfficerRepositoryMock.findOneByProjectIdAndRole(projectId, ProjectParticipantRole.MONITORING_OFFICER)).thenReturn(Optional.of(monitoringOfficer));

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

        when(legacyMonitoringOfficerService.getMonitoringOfficer(projectId)).thenReturn(serviceFailure(CommonErrors.notFoundError(LegacyMonitoringOfficer.class)));

        ServiceResult<MonitoringOfficerResource> result = service.findMonitoringOfficerForProject(projectId);

        assertTrue(result.isFailure());
    }
}