package org.innovateuk.ifs.assessment.interview.workflow;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentResponseOutcome;
import org.innovateuk.ifs.interview.repository.InterviewAssignmentPanelRepository;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.interview.workflow.configuration.InterviewAssignmentWorkflowHandler;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.TestableTransitionWorkflowAction;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ActivityType;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelMessageOutcomeBuilder.newAssessmentInterviewPanelMessageOutcome;
import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelResponseOutcomeBuilder.newAssessmentInterviewPanelResponseOutcome;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class InterviewPanelWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<
        InterviewAssignmentWorkflowHandler,
        InterviewAssignmentPanelRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType ACTIVITY_TYPE = ActivityType.ASSESSMENT_INTERVIEW_PANEL;

    @Autowired
    private InterviewAssignmentWorkflowHandler workflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private InterviewAssignmentPanelRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        repositoryMock = (InterviewAssignmentPanelRepository) mockSupplier.apply(InterviewAssignmentPanelRepository.class);
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
        return newAssessmentInterviewPanelMessageOutcome()
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
        return newAssessmentInterviewPanelResponseOutcome()
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
    protected Class<InterviewAssignmentPanelRepository> getProcessRepositoryType() {
        return InterviewAssignmentPanelRepository.class;
    }

    private ActivityType getActivityType() {
        return ACTIVITY_TYPE;
    }

    private InterviewAssignmentPanelRepository getRepositoryMock() {
        return repositoryMock;
    }

    private InterviewAssignment buildWorkflowProcessWithInitialState(InterviewAssignmentState initialState) {
        return newAssessmentInterviewPanel().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(InterviewAssignmentState initialState, InterviewAssignmentState expectedState, Function<InterviewAssignment, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialState, expectedState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(InterviewAssignmentState initialState, InterviewAssignmentState expectedState, Function<InterviewAssignment, Boolean> workflowHandlerMethod, Consumer<InterviewAssignment> additionalVerifications) {
        InterviewAssignment workflowProcess = buildWorkflowProcessWithInitialState(initialState);
        when(getRepositoryMock().findOneByTargetId(workflowProcess.getId())).thenReturn(workflowProcess);

        ActivityState expectedActivityState = new ActivityState(getActivityType(), expectedState.getBackingState());
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(getActivityType(), expectedState.getBackingState())).thenReturn(expectedActivityState);

        assertTrue(workflowHandlerMethod.apply(workflowProcess));

        assertEquals(expectedState, workflowProcess.getActivityState());

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(getActivityType(), expectedState.getBackingState());
        verify(getRepositoryMock()).save(workflowProcess);

        if (additionalVerifications != null) {
            additionalVerifications.accept(workflowProcess);
        }

        verifyNoMoreInteractionsWithMocks();
    }
}