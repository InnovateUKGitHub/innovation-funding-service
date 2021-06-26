package org.innovateuk.ifs.monitoring.transactional;

import org.innovateuk.ifs.BaseServiceUnitTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.mapper.ProjectMapper;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.project.monitoring.domain.MonitoringOfficer;
import org.innovateuk.ifs.project.monitoring.repository.MonitoringOfficerRepository;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerInviteService;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerReviewNotificationService;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerService;
import org.innovateuk.ifs.project.monitoring.transactional.MonitoringOfficerServiceImpl;
import org.innovateuk.ifs.project.monitoringofficer.transactional.LegacyMonitoringOfficerService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.user.mapper.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.monitoring.builder.MonitoringOfficerBuilder.newMonitoringOfficer;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class MonitoringOfficerServiceImplTest extends BaseServiceUnitTest<MonitoringOfficerService> {

    @Override
    protected MonitoringOfficerService supplyServiceUnderTest() {
        return new MonitoringOfficerServiceImpl();
    }

    @Mock
    private MonitoringOfficerRepository monitoringOfficerRepository;

    @Mock
    private MonitoringOfficerInviteService monitoringOfficerInviteService;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private LegacyMonitoringOfficerService legacyMonitoringOfficerService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private MonitoringOfficerReviewNotificationService monitoringOfficerReviewNotificationService;

    private long userId = 1L;

    private List<ProjectState> projectStates;

    private Project projectInSetup;

    private Project projectLive;

    private List<MonitoringOfficer> projectMonitoringOfficers;

    private ProjectResource projectResourceInSetup;

    private ProjectResource projectResourceLive;

    @Before
    public void setup() {
        projectInSetup = newProject()
                .withProjectProcess(newProjectProcess()
                        .withActivityState(ProjectState.SETUP)
                        .build())
                .build();

        projectLive = newProject()
                .withProjectProcess(newProjectProcess()
                        .withActivityState(ProjectState.LIVE)
                        .build())
                .build();

        projectMonitoringOfficers = newMonitoringOfficer()
                .withProject(projectInSetup, projectLive)
                .build(2);

        projectResourceInSetup = newProjectResource()
                .withProjectState(ProjectState.SETUP)
                .build();

        projectResourceLive = newProjectResource()
                .withProjectState(ProjectState.LIVE)
                .build();
    }

    @Test
    public void filterMonitoringOfficerProjectsAppliesFilter() {
        projectStates = asList(ProjectState.LIVE, ProjectState.WITHDRAWN, ProjectState.COMPLETED_OFFLINE,
                ProjectState.UNSUCCESSFUL, ProjectState.SETUP, ProjectState.HANDLED_OFFLINE, ProjectState.ON_HOLD);

        when(monitoringOfficerRepository.filterMonitoringOfficerProjects(userId, projectStates)).thenReturn(projectMonitoringOfficers);
        when(projectMapper.mapToResource(projectInSetup)).thenReturn(projectResourceInSetup);
        when(projectMapper.mapToResource(projectLive)).thenReturn(projectResourceLive);

        ServiceResult<List<ProjectResource>> result = service.filterMonitoringOfficerProjects(userId, true, true, false, true, false);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertThat(result.getSuccess(), containsInAnyOrder(projectResourceInSetup, projectResourceLive));
    }

    @Test
    public void filterMonitoringOfficerProjectsReturnsDefault() {
        projectStates = asList(ProjectState.SETUP, ProjectState.LIVE, ProjectState.WITHDRAWN, ProjectState.HANDLED_OFFLINE,
                ProjectState.COMPLETED_OFFLINE, ProjectState.ON_HOLD, ProjectState.UNSUCCESSFUL);

        when(monitoringOfficerRepository.filterMonitoringOfficerProjects(userId, projectStates)).thenReturn(projectMonitoringOfficers);
        when(projectMapper.mapToResource(projectInSetup)).thenReturn(projectResourceInSetup);
        when(projectMapper.mapToResource(projectLive)).thenReturn(projectResourceLive);

        ServiceResult<List<ProjectResource>> result = service.filterMonitoringOfficerProjects(userId, false, false, false, false, false);

        assertTrue(result.isSuccess());
        assertEquals(2, result.getSuccess().size());
        assertThat(result.getSuccess(), containsInAnyOrder(projectResourceInSetup, projectResourceLive));
    }
}