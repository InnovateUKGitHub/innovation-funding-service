package org.innovateuk.ifs.assessment.interview.workflow;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelMessageOutcome;
import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanelResponseOutcome;
import org.innovateuk.ifs.assessment.interview.repository.AssessmentInterviewPanelRepository;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.assessment.interview.workflow.configuration.AssessmentInterviewPanelWorkflowHandler;
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
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class AssessmentInterviewPanelWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<
        AssessmentInterviewPanelWorkflowHandler,
        AssessmentInterviewPanelRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType ACTIVITY_TYPE = ActivityType.ASSESSMENT_INTERVIEW_PANEL;

    @Autowired
    private AssessmentInterviewPanelWorkflowHandler workflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private AssessmentInterviewPanelRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        repositoryMock = (AssessmentInterviewPanelRepository) mockSupplier.apply(AssessmentInterviewPanelRepository.class);
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

    private AssessmentInterviewPanelMessageOutcome messageOutcome() {
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

    private AssessmentInterviewPanelResponseOutcome responseOutcome() {
        return newAssessmentInterviewPanelResponseOutcome()
                .withResponse("response")
                .build();
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<AssessmentInterviewPanelWorkflowHandler> getWorkflowHandlerType() {
        return AssessmentInterviewPanelWorkflowHandler.class;
    }

    @Override
    protected Class<AssessmentInterviewPanelRepository> getProcessRepositoryType() {
        return AssessmentInterviewPanelRepository.class;
    }

    private ActivityType getActivityType() {
        return ACTIVITY_TYPE;
    }

    private AssessmentInterviewPanelRepository getRepositoryMock() {
        return repositoryMock;
    }

    private AssessmentInterviewPanel buildWorkflowProcessWithInitialState(AssessmentInterviewPanelState initialState) {
        return newAssessmentInterviewPanel().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentInterviewPanelState initialState, AssessmentInterviewPanelState expectedState, Function<AssessmentInterviewPanel, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialState, expectedState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentInterviewPanelState initialState, AssessmentInterviewPanelState expectedState, Function<AssessmentInterviewPanel, Boolean> workflowHandlerMethod, Consumer<AssessmentInterviewPanel> additionalVerifications) {
        AssessmentInterviewPanel workflowProcess = buildWorkflowProcessWithInitialState(initialState);
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