package org.innovateuk.ifs.project.grantofferletter.workflow;

import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.grantofferletter.configuration.workflow.GrantOfferLetterWorkflowHandler;
import org.innovateuk.ifs.project.grantofferletter.domain.GOLProcess;
import org.innovateuk.ifs.project.grantofferletter.repository.GrantOfferLetterProcessRepository;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
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

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.core.builder.ProjectBuilder.newProject;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.innovateuk.ifs.project.core.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GrantOfferLetterWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<GrantOfferLetterWorkflowHandler, GrantOfferLetterProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private GrantOfferLetterWorkflowHandler golWorkflowHandler;
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepositoryMock;
    private ProjectUserRepository projectUserRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        grantOfferLetterProcessRepositoryMock = (GrantOfferLetterProcessRepository) mockSupplier.apply(GrantOfferLetterProcessRepository.class);
        projectUserRepositoryMock = (ProjectUserRepository) mockSupplier.apply(ProjectUserRepository.class);
    }

    @Test
    public void projectCreated() {
        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here
        boolean result = golWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, GrantOfferLetterState.PENDING);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(GrantOfferLetterEvent.PROJECT_CREATED.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);
    }

    @Test
    public void grantOfferLetterRemoved() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.removeGrantOfferLetter(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.PENDING, GrantOfferLetterState.PENDING, GrantOfferLetterEvent.GOL_REMOVED);
    }

    @Test
    public void grantOfferLetterReset() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.grantOfferLetterReset(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.SENT, GrantOfferLetterState.PENDING, GrantOfferLetterEvent.GOL_RESET);

    }
    @Test
    public void grantOfferLetterRemovedNotAllowedInNonPendingStates() {

        asList(GrantOfferLetterState.values()).forEach(startingState -> {

            if (startingState != GrantOfferLetterState.PENDING) {
                callWorkflowAndCheckTransitionFailsInternalUser(((project, internalUser) -> golWorkflowHandler.removeGrantOfferLetter(project, internalUser)), startingState);
            }
        });
    }

    @Test
    public void signedGrantOfferLetterRemovedNotAllowedInNonSentStates() {

        asList(GrantOfferLetterState.values()).forEach(startingState -> {

            if (startingState != GrantOfferLetterState.SENT) {

                callWorkflowAndCheckTransitionFailsExternalUser(((project, projectUser) -> {

                    when(projectUserRepositoryMock.findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId())).thenReturn(projectUser);
                    boolean removed = golWorkflowHandler.removeSignedGrantOfferLetter(project, projectUser.getUser());
                    verify(projectUserRepositoryMock).findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId());

                    return removed;

                }), startingState);
            }
        });
    }

    @Test
    public void grantOfferLetterSent() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.grantOfferLetterSent(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.PENDING, GrantOfferLetterState.SENT, GrantOfferLetterEvent.GOL_SENT);
    }

    @Test
    public void grantOfferLetterSigned() {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> golWorkflowHandler.grantOfferLetterSigned(project, projectUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.SENT, GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterEvent.GOL_SIGNED);
    }

    @Test
    public void grantOfferLetterRejected() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.grantOfferLetterRejected(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterState.SENT, GrantOfferLetterEvent.SIGNED_GOL_REJECTED);
    }

    @Test
    public void grantOfferLetterApproved() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.grantOfferLetterApproved(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterState.APPROVED, GrantOfferLetterEvent.SIGNED_GOL_APPROVED);
    }

    @Test
    public void approveSignedGrantOfferLetter() {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> golWorkflowHandler.grantOfferLetterApproved(project, internalUser)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterState.APPROVED, GrantOfferLetterEvent.SIGNED_GOL_APPROVED);
    }

    @Test
    public void signGrantOfferLetterWithoutProjectUser() {

        callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser((project -> golWorkflowHandler.sign(project)),

                // current State, destination State and expected Event to be fired
                GrantOfferLetterState.SENT, GrantOfferLetterState.READY_TO_APPROVE, GrantOfferLetterEvent.GOL_SIGNED);
    }

    @Test
    public void signedGrantOfferLetterRemoved() {

        callWorkflowAndCheckTransitionAndEventFired((project, projectUser) -> {

            when(projectUserRepositoryMock.findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId())).thenReturn(projectUser);
            boolean removed = golWorkflowHandler.removeSignedGrantOfferLetter(project, projectUser.getUser());
            verify(projectUserRepositoryMock).findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId());

            return removed;
        },
        // current State, destination State and expected Event to be fired
        GrantOfferLetterState.SENT, GrantOfferLetterState.SENT, GrantOfferLetterEvent.SIGNED_GOL_REMOVED);
    }

    @Test
    public void signedGrantOfferLetterNotRemovedIfNotProjectManager() {

        callWorkflowAndCheckTransitionFailsExternalUser((project, projectUser) -> {

            when(projectUserRepositoryMock.findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId())).thenReturn(null);
            boolean removed = golWorkflowHandler.removeSignedGrantOfferLetter(project, projectUser.getUser());
            verify(projectUserRepositoryMock).findByProjectIdAndRoleAndUserId(project.getId(), PROJECT_MANAGER, projectUser.getUser().getId());

            return removed;
        }, GrantOfferLetterState.SENT);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, GrantOfferLetterState currentGOLState, GrantOfferLetterState destinationGOLState, GrantOfferLetterEvent expectedEventToBeFired) {

        Project project = newProject().build();
        User user = newUser().build();
        ProjectUser projectUser = newProjectUser().withUser(user).build();

        // Set the current state in the GOL Process
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, currentGOLState);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, destinationGOLState);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredInternalUser(BiFunction<Project, User, Boolean> workflowMethodToCall, GrantOfferLetterState currentGOLState, GrantOfferLetterState destinationGOLState, GrantOfferLetterEvent expectedEventToBeFired) {

        Project project = newProject().build();
        User internalUser = newUser().build();

        // Set the current state in the GOL Process
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, currentGOLState);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(internalUser, project, destinationGOLState);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);
    }

    private void callWorkflowAndCheckTransitionFailsInternalUser(BiFunction<Project, User, Boolean> workflowMethodToCall, GrantOfferLetterState currentGOLState) {

        Project project = newProject().build();
        User internalUser = newUser().build();

        // Set the current state in the GOL Process
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, currentGOLState);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertFalse(result);

        verify(grantOfferLetterProcessRepositoryMock, never()).save(isA(GOLProcess.class));
    }

    private void callWorkflowAndCheckTransitionFailsExternalUser(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, GrantOfferLetterState currentGOLState) {

        Project project = newProject().build();
        User externalUser = newUser().build();
        ProjectUser projectUser = newProjectUser().withUser(externalUser).build();

        // Set the current state in the GOL Process
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, currentGOLState);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertFalse(result);

        verify(grantOfferLetterProcessRepositoryMock, never()).save(isA(GOLProcess.class));
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser(Function<Project, Boolean> workflowMethodToCall, GrantOfferLetterState currentGOLState, GrantOfferLetterState destinationGOLState, GrantOfferLetterEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the GOL Process
        GOLProcess currentGOLProcess = new GOLProcess((ProjectUser) null, project, currentGOLState);
        currentGOLProcess.setParticipant(projectUser);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, destinationGOLState);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<GrantOfferLetterWorkflowHandler> getWorkflowHandlerType() {
        return GrantOfferLetterWorkflowHandler.class;
    }

    @Override
    protected Class<GrantOfferLetterProcessRepository> getProcessRepositoryType() {
        return GrantOfferLetterProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(GrantOfferLetterProcessRepository.class);
        repositories.add(ProjectUserRepository.class);
        return repositories;
    }
}