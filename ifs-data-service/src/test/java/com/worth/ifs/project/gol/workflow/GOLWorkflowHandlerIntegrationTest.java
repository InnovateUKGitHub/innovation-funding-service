package com.worth.ifs.project.gol.workflow;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.gol.domain.GOLProcess;
import com.worth.ifs.project.gol.repository.GrantOfferLetterProcessRepository;
import com.worth.ifs.project.gol.resource.GOLOutcomes;
import com.worth.ifs.project.gol.resource.GOLState;
import com.worth.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import com.worth.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.workflow.domain.ActivityType.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GOLWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<GOLWorkflowHandler, GrantOfferLetterProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private GrantOfferLetterProcessRepository grantOfferLetterProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        grantOfferLetterProcessRepositoryMock = (GrantOfferLetterProcessRepository) mockSupplier.apply(GrantOfferLetterProcessRepository.class);
    }

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_GRANT_OFFER_LETTER, GOLState.PENDING.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_GRANT_OFFER_LETTER, GOLState.PENDING.getBackingState())).thenReturn(expectedActivityState);


        // Call the workflow here
        boolean result = golWorkflowHandler.projectCreated(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(GOLOutcomes.PROJECT_CREATED.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);

    }

    @Test
    public void testGrantOfferLetterSent() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> golWorkflowHandler.grantOfferLetterSent(project, projectUser)),

                // current State, destination State and expected Event to be fired
                GOLState.PENDING, GOLState.SENT, GOLOutcomes.GOL_SENT);
    }

    @Test
    public void testGrantOfferLetterSigned() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> golWorkflowHandler.grantOfferLetterSigned(project, projectUser)),

                // current State, destination State and expected Event to be fired
                GOLState.SENT, GOLState.READY_TO_APPROVE, GOLOutcomes.GOL_SIGNED);
    }

    @Test
    public void testGrantOfferLetterRejected() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> golWorkflowHandler.grantOfferLetterRejected(project, projectUser)),

                // current State, destination State and expected Event to be fired
                GOLState.READY_TO_APPROVE, GOLState.PENDING, GOLOutcomes.GOL_REJECTED);
    }

    @Test
    public void testGrantOfferLetterApproved() throws Exception {

        callWorkflowAndCheckTransitionAndEventFired(((project, projectUser) -> golWorkflowHandler.grantOfferLetterApproved(project, projectUser)),

                // current State, destination State and expected Event to be fired
                GOLState.READY_TO_APPROVE, GOLState.APPROVED, GOLOutcomes.GOL_APPROVED);
    }

    private void callWorkflowAndCheckTransitionAndEventFired(BiFunction<Project, ProjectUser, Boolean> workflowMethodToCall, GOLState currentGOLState, GOLState destinationGOLState, GOLOutcomes expectedEventToBeFired) {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Set the current state in the GOL Process
        ActivityState currentActivityState = new ActivityState(PROJECT_SETUP_GRANT_OFFER_LETTER, currentGOLState.getBackingState());
        GOLProcess currentGOLProcess = new GOLProcess(null, project, currentActivityState);
        when(grantOfferLetterProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(currentGOLProcess);

        // Set the destination state which we expect when the event is fired
        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_GRANT_OFFER_LETTER, destinationGOLState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_GRANT_OFFER_LETTER, destinationGOLState.getBackingState())).thenReturn(expectedActivityState);

        // Call the workflow here
        boolean result = workflowMethodToCall.apply(project, projectUser);

        assertTrue(result);

        // Once the workflow is called, check that the correct details (state. events etc) are updated in the process table.
        // This can be done by building the expected GOLProcess object (say X) and verifying that X was the object that was saved.
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, expectedActivityState);

        // Ensure the correct event was fired by the workflow
        expectedGolProcess.setProcessEvent(expectedEventToBeFired.getType());

        verify(grantOfferLetterProcessRepositoryMock).save(expectedGolProcess);
    }

    @Override
    protected Class getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<GOLWorkflowHandler> getWorkflowHandlerType() {
        return GOLWorkflowHandler.class;
    }

    @Override
    protected Class<GrantOfferLetterProcessRepository> getProcessRepositoryType() {
        return GrantOfferLetterProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(GrantOfferLetterProcessRepository.class);
        repositories.add(ActivityStateRepository.class);
        return repositories;
    }
}