package org.innovateuk.ifs.project.core.workflow.guard;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.workflow.configuration.guard.ProjectInSetupGuard;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.resource.ProjectState.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ProjectInSetupGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private ProjectInSetupGuard projectInSetupGuard = new ProjectInSetupGuard();

    @Test
    public void evaluate_ProjectIsInLive() {
        Project project = newProject().withProjectProcess(newProjectProcess().withActivityState(LIVE).build()).build();
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
    }

    @Test
    public void evaluate_ProjectIsInSetup() {
        Project project = newProject().withProjectProcess(newProjectProcess().withActivityState(SETUP).build()).build();
        assertTrue(projectInSetupGuard.evaluate(setupContext(project)));
    }

    @Test
    public void evaluate_ProjectIsWithdrawn() {
        Project project = newProject().withProjectProcess(newProjectProcess().withActivityState(WITHDRAWN).build()).build();
        assertFalse(projectInSetupGuard.evaluate(setupContext(project)));
    }

    private StateContext<ProjectState, ProjectEvent> setupContext(Project project) {
        StateContext<ProjectState, ProjectEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("target")).thenReturn(project);
        return context;
    }
}