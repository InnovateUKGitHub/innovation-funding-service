package org.innovateuk.ifs.project.core.workflow.guard;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.core.workflow.configuration.guard.ProjectInSetupGuard;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ProjectInSetupGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInSetupGuard projectInSetupGuard = new ProjectInSetupGuard();

    @Test
    public void evaluate_ProjectIsInLive() {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(ProjectState.LIVE)
                .build();
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
        verify(projectProcessRepositoryMock, only()).findOneByTargetId(project.getId());
    }

    @Test
    public void evaluate_ProjectIsInSetup() {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(ProjectState.SETUP)
                .build();
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
        verify(projectProcessRepositoryMock, only()).findOneByTargetId(project.getId());
    }

    @Test
    public void evaluate_ProjectIsWithdrawn() {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(ProjectState.WITHDRAWN)
                .build();
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertFalse(projectInSetupGuard.evaluate(setupContext(project)));
        verify(projectProcessRepositoryMock, only()).findOneByTargetId(project.getId());
    }

    private StateContext<ProjectState, ProjectEvent> setupContext(Project project) {
        StateContext<ProjectState, ProjectEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("target")).thenReturn(project);
        return context;
    }
}