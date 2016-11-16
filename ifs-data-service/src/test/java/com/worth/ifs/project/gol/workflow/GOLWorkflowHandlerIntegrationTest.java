package com.worth.ifs.project.gol.workflow;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.gol.domain.GOLProcess;
import com.worth.ifs.project.gol.repository.GOLProcessRepository;
import com.worth.ifs.project.gol.resource.GOLOutcomes;
import com.worth.ifs.project.gol.resource.GOLState;
import com.worth.ifs.project.gol.workflow.configuration.GOLWorkflowHandler;
import com.worth.ifs.project.workflow.projectdetails.actions.BaseProjectDetailsAction;
import com.worth.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import com.worth.ifs.workflow.TestableTransitionWorkflowAction;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import com.worth.ifs.workflow.resource.State;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.project.builder.ProjectBuilder.newProject;
import static com.worth.ifs.project.builder.ProjectUserBuilder.newProjectUser;
import static com.worth.ifs.workflow.domain.ActivityType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GOLWorkflowHandlerIntegrationTest extends
        BaseWorkflowHandlerIntegrationTest<GOLWorkflowHandler, GOLProcessRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private GOLWorkflowHandler golWorkflowHandler;
    private ActivityStateRepository activityStateRepositoryMock;
    private GOLProcessRepository golProcessRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        golProcessRepositoryMock = (GOLProcessRepository) mockSupplier.apply(GOLProcessRepository.class);
    }

    // This is what I wanted to test.

    @Test
    public void testProjectCreated() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        // Call the workflow here and
        boolean result = golWorkflowHandler.projectCreated(project, projectUser);

        ActivityState expectedActivityState = new ActivityState(PROJECT_SETUP_GRANT_OFFER_LETTER, State.PENDING);
        GOLProcess expectedGolProcess = new GOLProcess(projectUser, project, expectedActivityState);

        // Expect that the GOL Process is updated with the current state.
        // This test case gives the exception - Caused by: com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException: Column 'activity_state_id' cannot be null
        //at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method) ~[na:1.8.0_101]

        verify(golProcessRepositoryMock).save(expectedGolProcess);

    }

    // This is how its tested in Project Details and Finance Checks - so tried to do the same thing here:
    @Test
    public void testProjectCreatedNew() throws Exception {

        Project project = newProject().build();
        ProjectUser projectUser = newProjectUser().build();

        ActivityState pendingState = new ActivityState(PROJECT_SETUP_GRANT_OFFER_LETTER, State.PENDING);
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(PROJECT_SETUP_GRANT_OFFER_LETTER, State.PENDING)).thenReturn(pendingState);

        // this first step will not have access to an existing Process, because it's just starting
        when(golProcessRepositoryMock.findOneByTargetId(project.getId())).thenReturn(null);

        // now call the method under test
        assertTrue(golWorkflowHandler.projectCreated(project, projectUser));

        verify(golProcessRepositoryMock).findOneByTargetId(project.getId());

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(PROJECT_SETUP_GRANT_OFFER_LETTER, State.PENDING);

        verify(golProcessRepositoryMock).save(
                processExpectations(project.getId(), projectUser.getId(), GOLState.PENDING, GOLOutcomes.PROJECT_CREATED));

        verifyNoMoreInteractionsWithMocks();
    }

    private GOLProcess processExpectations(Long expectedProjectId, Long expectedProjectUserId, GOLState expectedState, GOLOutcomes expectedEvent) {
        return createLambdaMatcher(process -> {
            assertProcessState(expectedProjectId, expectedProjectUserId, expectedState, expectedEvent, process);
        });
    }

    private void assertProcessState(Long expectedProjectId, Long expectedProjectUserId, GOLState expectedState, GOLOutcomes expectedEvent, GOLProcess process) {
        assertEquals(expectedProjectId, process.getTarget().getId());
        assertEquals(expectedProjectUserId, process.getParticipant().getId());
        assertEquals(expectedState, process.getActivityState());
        assertEquals(expectedEvent.getType(), process.getProcessEvent());
    }

    @Override
    protected Class getBaseActionType() {
        return BaseProjectDetailsAction.class; // Replaced this with TestableTransitionWorkflowAction and checked - but still same problem
    }

    @Override
    protected Class<GOLWorkflowHandler> getWorkflowHandlerType() {
        return GOLWorkflowHandler.class;
    }

    @Override
    protected Class<GOLProcessRepository> getProcessRepositoryType() {
        return GOLProcessRepository.class;
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(GOLProcessRepository.class);
        return repositories;
    }
}