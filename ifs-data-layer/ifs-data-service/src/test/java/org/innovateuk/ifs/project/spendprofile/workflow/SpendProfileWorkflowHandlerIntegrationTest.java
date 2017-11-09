package org.innovateuk.ifs.project.spendprofile.workflow;

import org.innovateuk.ifs.project.domain.Project;
import org.innovateuk.ifs.project.domain.ProjectUser;
import org.innovateuk.ifs.project.spendprofile.configuration.workflow.SpendProfileWorkflowHandler;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfileProcess;
import org.innovateuk.ifs.project.spendprofile.repository.SpendProfileProcessRepository;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileEvent;
import org.innovateuk.ifs.project.spendprofile.resource.SpendProfileState;
import org.innovateuk.ifs.user.domain.User;
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
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.innovateuk.ifs.workflow.domain.ActivityType.PROJECT_SETUP_SPEND_PROFILE;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpendProfileWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<SpendProfileWorkflowHandler, SpendProfileProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private SpendProfileWorkflowHandler spendProfileWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private SpendProfileProcessRepository spendProfileProcessRepositoryMock;

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, SpendProfileState.PENDING.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, SpendProfileState.PENDING.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = spendProfileWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state, events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(SpendProfileEvent.PROJECT_CREATED.getType());

        verify(spendProfileProcessRepositoryMock).save(expectedSpendProfileProcess);
    }

    @Test
    public void testSpendProfileGenerated() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.sendProfileGenerated(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.PENDING, SpendProfileState.CREATED, SpendProfileEvent.SPEND_PROFILE_GENERATED);
    }

    @Test
    public void testSpendProfileSubmitted() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void testSpendProfileApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileApproved(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.APPROVED, SpendProfileEvent.SPEND_PROFILE_APPROVED);
    }

    @Test
    public void testSpendProfileRejected() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredInternalUser(((project, internalUser) -> spendProfileWorkflowHandler.spendProfileRejected(project, internalUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.SUBMITTED, SpendProfileState.REJECTED, SpendProfileEvent.SPEND_PROFILE_REJECTED);
    }

    @Test
    public void testSpendProfileRejectedIsSubmitted() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(((project, projectUser) -> spendProfileWorkflowHandler.spendProfileSubmitted(project, projectUser)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.REJECTED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    @Test
    public void testSubmitSpendProfileWithoutProjectUser() throws Exception {

        callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser((project -> spendProfileWorkflowHandler.submit(project)),

                // current State, destination State and expected Event to be fired
                SpendProfileState.CREATED, SpendProfileState.SUBMITTED, SpendProfileEvent.SPEND_PROFILE_SUBMITTED);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredInternalUser(BiFunction<Project, User, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        User internalUser = newUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentActivityState);
        when(spendProfileProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, internalUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(internalUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepositoryMock).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithoutProjectUser(Function<Project, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess((ProjectUser) null, project, currentActivityState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepositoryMock).save(expectedSpendProfileProcess);
    }

    private void callWorkflowAndCheckTransitionAndEventFiredWithProjectUser(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, SpendProfileState currentSpendProfileState, SpendProfileState destinationSpendProfileState, SpendProfileEvent expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the Spend Profile Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, currentSpendProfileState.getBackingState());
        SpendProfileProcess currentSpendProfileProcess = new SpendProfileProcess(projectUser, project, currentActivityState);
        currentSpendProfileProcess.setParticipant(projectUser);
        when(spendProfileProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentSpendProfileProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_SPEND_PROFILE, destinationSpendProfileState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected SpendProfileProcess object (say X) and verifying that X was the object that was saved.
        SpendProfileProcess expectedSpendProfileProcess = new SpendProfileProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedSpendProfileProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(spendProfileProcessRepositoryMock).save(expectedSpendProfileProcess);
    }

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        spendProfileProcessRepositoryMock = (SpendProfileProcessRepository) mockSupplier.apply(SpendProfileProcessRepository.class);
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<SpendProfileWorkflowHandler> getWorkflowHandlerType() {
        return SpendProfileWorkflowHandler.class;
    }

    @Override
    protected Class<SpendProfileProcessRepository> getProcessRepositoryType() {
        return SpendProfileProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(SpendProfileProcessRepository.class);
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}
