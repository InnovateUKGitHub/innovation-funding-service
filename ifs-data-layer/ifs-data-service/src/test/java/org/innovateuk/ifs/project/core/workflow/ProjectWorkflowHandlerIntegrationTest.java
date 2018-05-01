package org.innovateuk.ifs.project.core.workflow;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectProcess;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectProcessRepository;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProjectWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<ProjectWorkflowHandler, ProjectProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;
    private ProjectProcessRepository projectProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        projectProcessRepositoryMock = (ProjectProcessRepository) mockSupplier.apply(ProjectProcessRepository.class);
    }

    @Test
    public void testProjectCreated() {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = projectWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ProjectProcess object (say X) and verifying that X was the object that was saved.
        ProjectProcess expectedProjectProcess = new ProjectProcess(projectUser, project, ProjectState.SETUP);

        // Ensure the correct event was fired by the workflow
        expectedProjectProcess.setProcessEvent(ProjectEvent.PROJECT_CREATED.getType());

        verify(projectProcessRepositoryMock).save(expectedProjectProcess);

    }

    @Test
    public void testGrantOfferLetterApproved() {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> projectWorkflowHandler.grantOfferLetterApproved(project, projectUser)),

                // current State, destination State and expected Event to be fired
                ProjectState.SETUP, ProjectState.LIVE, ProjectEvent.GOL_APPROVED);
    }

    @Test
    public void testProjectWithdrawn() {

        callWorkflowAndCheckTransitionAndEventFiredWithInternalUserParticipant(
                ((project, internalUser) -> projectWorkflowHandler.projectWithdrawn(project, internalUser)),

                // current State, destination State and expected Event to be fired
                ProjectState.SETUP, ProjectState.WITHDRAWN, ProjectEvent.PROJECT_WITHDRAWN);
    }


    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, ProjectState currentProjectState, ProjectState destinationProjectState, ProjectEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Project Process
        ProjectProcess currentProjectProcess = setUpCurrentProjectProcess(projectUser, project, currentProjectState);
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentProjectProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected ProjectProcess object (say X) and verifying that X was the object that was saved.
        ProjectProcess expectedProjectProcess = setUpExpectedProjectProcess(projectUser, project, destinationProjectState, expectedEventToBeFired);

        verify(projectProcessRepositoryMock).save(expectedProjectProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithInternalUserParticipant(BiFunction<Project, User, Boolean> workflowMethodToCall, ProjectState currentProjectState, ProjectState destinationProjectState, ProjectEvent expectedEventToBeFired) {
        Project project = newProject().build();
        User internalUser = newUser().build();

        ProjectProcess currentProjectProcess = setUpCurrentProjectProcess(null, project, currentProjectState);
        when(projectProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentProjectProcess);

        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertTrue(result);

        ProjectProcess expectedProjectProcess = setUpExpectedProjectProcess(null, project, destinationProjectState, expectedEventToBeFired);

        verify(projectProcessRepositoryMock).save(expectedProjectProcess);
    }

    private ProjectProcess setUpCurrentProjectProcess(ProjectUser projectUser, Project project, ProjectState currentProjectState) {
        return new ProjectProcess(projectUser, project, currentProjectState);
    }

    private ProjectProcess setUpExpectedProjectProcess(ProjectUser projectUser, Project project, ProjectState expectedActivityState, ProjectEvent eventToBeFired) {
        ProjectProcess expectedProjectProcess = new ProjectProcess(projectUser, project, expectedActivityState);
        expectedProjectProcess.setProcessEvent(eventToBeFired.getType());
        return expectedProjectProcess;
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
        return repositories;
    }
}