package org.innovateuk.ifs.project.workflow.guards;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.workflow.configuration.guards.ProjectInSetupGuard;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.PENDING;
import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectProcessBuilder.newProjectProcess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
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
