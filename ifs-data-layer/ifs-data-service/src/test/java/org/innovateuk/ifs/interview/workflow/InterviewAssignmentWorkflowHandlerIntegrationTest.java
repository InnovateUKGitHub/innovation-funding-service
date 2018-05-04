package org.innovateuk.ifs.interview.workflow;

import org.innovateuk.ifs.interview.builder.InterviewAssignmentBuilder;
import org.innovateuk.ifs.interview.builder.InterviewAssignmentMessageOutcomeBuilder;
import org.innovateuk.ifs.interview.builder.InterviewAssignmentResponseOutcomeBuilder;
import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class InterviewAssignmentWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<InterviewAssignmentWorkflowHandler, InterviewAssignmentRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private InterviewAssignmentWorkflowHandler workflowHandler;

    private InterviewAssignmentRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        repositoryMock = (InterviewAssignmentRepository) mockSupplier.apply(InterviewAssignmentRepository.class);
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ProcessRoleRepository.class);
        return repositories;
    }

    @Test
    public void notifyInterviewPanel() {
        assertStateChangeOnWorkflowHandlerCall(CREATED, AWAITING_FEEDBACK_RESPONSE,
                invite -> workflowHandler.notifyInterviewPanel(invite, messageOutcome()),
                assessmentInterviewPanel ->
                        assertEquals(messageOutcome(), assessmentInterviewPanel.getMessage())
        );
    }

    private InterviewAssignmentMessageOutcome messageOutcome() {
        return InterviewAssignmentMessageOutcomeBuilder.newInterviewAssignmentMessageOutcome()
                .withSubject("subject")
                .withMessage("message")
                .build();
    }

    @Test
    public void respondToInterviewPanel() {
        assertStateChangeOnWorkflowHandlerCall(AWAITING_FEEDBACK_RESPONSE, SUBMITTED_FEEDBACK_RESPONSE,
                invite -> workflowHandler.respondToInterviewPanel(invite, responseOutcome()),
                assessmentInterviewPanel ->
                        assertEquals(responseOutcome(), assessmentInterviewPanel.getResponse())
        );
    }

    private InterviewAssignmentResponseOutcome responseOutcome() {
        return InterviewAssignmentResponseOutcomeBuilder.newInterviewAssignmentResponseOutcome()
                .withResponse("response")
                .build();
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<InterviewAssignmentWorkflowHandler> getWorkflowHandlerType() {
        return InterviewAssignmentWorkflowHandler.class;
    }

    @Override
    protected Class<InterviewAssignmentRepository> getProcessRepositoryType() {
        return InterviewAssignmentRepository.class;
    }

    private InterviewAssignmentRepository getRepositoryMock() {
        return repositoryMock;
    }

    private InterviewAssignment buildWorkflowProcessWithInitialState(InterviewAssignmentState initialState) {
        return InterviewAssignmentBuilder.newInterviewAssignment().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(InterviewAssignmentState initialState, InterviewAssignmentState expectedState, Function<InterviewAssignment, Boolean> workflowHandlerMethod, Consumer<InterviewAssignment> additionalVerifications) {
        InterviewAssignment workflowProcess = buildWorkflowProcessWithInitialState(initialState);
        when(getRepositoryMock().findOneByTargetId(workflowProcess.getId())).thenReturn(workflowProcess);

        assertTrue(workflowHandlerMethod.apply(workflowProcess));

        assertEquals(expectedState, workflowProcess.getProcessState());

        verify(getRepositoryMock()).save(workflowProcess);

        if (additionalVerifications != null) {
            additionalVerifications.accept(workflowProcess);
        }

        verifyNoMoreInteractionsWithMocks();
    }
}