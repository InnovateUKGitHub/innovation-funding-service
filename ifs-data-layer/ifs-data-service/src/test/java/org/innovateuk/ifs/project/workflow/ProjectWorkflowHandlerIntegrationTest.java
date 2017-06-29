package org.innovateuk.ifs.project.workflow;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectProcess;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectOutcomes;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.project.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ProjectWorkflowHandler, ProjectProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        projectProcessRepositoryMock = (ProjectProcessRepository) mockSupplier.apply(ProjectProcessRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP, ProjectState.SETUP.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP, ProjectState.SETUP.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = projectWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ProjectProcess object (say X) and verifying that X was the object that was saved.
        ProjectProcess expectedProjectProcess = new ProjectProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedProjectProcess.setProcessEvent(ProjectOutcomes.PROJECT_CREATED.getType());

        verify(projectProcessRepositoryMock).save(expectedProjectProcess);

    }

    @Test
    public void testGrantOfferLetterApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> projectWorkflowHandler.grantOfferLetterApproved(project, projectUser)),

                // current State, destination State and expected Event to be fired
                ProjectState.SETUP, ProjectState.LIVE, ProjectOutcomes.GOL_APPROVED);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, ProjectState currentProjectState, ProjectState destinationProjectState, ProjectOutcomes expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Project Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP, currentProjectState.getBackingState());
        ProjectProcess currentProjectProcess = new ProjectProcess(null, project, currentActivityState);
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentProjectProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP, destinationProjectState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP, destinationProjectState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ProjectProcess object (say X) and verifying that X was the object that was saved.
        ProjectProcess expectedProjectProcess = new ProjectProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedProjectProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(projectProcessRepositoryMock).save(expectedProjectProcess);
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ProjectWorkflowHandler> getWorkflowHandlerType() {
        return ProjectWorkflowHandler.class;
    }

    @Override
    protected Class<ProjectProcessRepository> getProcessRepositoryType() {
        return ProjectProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ProjectProcessRepository.class);
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}
