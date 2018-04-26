package org.innovateuk.ifs.project.core.workflow.guard;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.core.workflow.configuration.guard.ProjectInSetupGuard;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

public class ProjectInSetupGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInSetupGuard projectInSetupGuard = new ProjectInSetupGuard();

    @Test
    public void evaluate_ProjectIsInLive() throws Exception {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.LIVE.getBackingState()))
                .build();
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
        verify(projectProcessRepositoryMock, only()).findOneByTargetId(project.getId());
    }

    @Test
    public void evaluate_ProjectIsInSetup() throws Exception {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.SETUP.getBackingState()))
                .build();
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(projectProcess);
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
        verify(projectProcessRepositoryMock, only()).findOneByTargetId(project.getId());
    }

    @Test
    public void evaluate_ProjectIsWithdrawn() throws Exception {
        Project project = newProject().build();
        ProjectProcess projectProcess = newProjectProcess()
                .withActivityState(new ActivityState(PROJECT_SETUP, ProjectState.WITHDRAWN.getBackingState()))
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
