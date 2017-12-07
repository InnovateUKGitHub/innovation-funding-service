package org.innovateuk.ifs.assessment.panel.workflow;

import org.innovateuk.ifs.assessment.panel.domain.AssessmentReview;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.assessment.panel.repository.AssessmentReviewRepository;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.innovateuk.ifs.assessment.panel.workflow.configuration.AssessmentReviewWorkflowHandler;
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

import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewBuilder.newAssessmentReview;
import static org.innovateuk.ifs.assessment.panel.builder.AssessmentReviewRejectOutcomeBuilder.newAssessmentPanelApplicationInviteRejectOutcome;
import static org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class AssessmentReviewWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<
        AssessmentReviewWorkflowHandler,
        AssessmentReviewRepository, TestableTransitionWorkflowAction> {

    private static final ActivityType ACTIVITY_TYPE = ActivityType.ASSESSMENT_PANEL_APPLICATION_INVITE;

    @Autowired
    private AssessmentReviewWorkflowHandler workflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private AssessmentReviewRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        repositoryMock = (AssessmentReviewRepository) mockSupplier.apply(AssessmentReviewRepository.class);
    }

    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ProcessRoleRepository.class);
        return repositories;
    }

    @Test
    public void notifyInvitation() {
        assertStateChangeOnWorkflowHandlerCall(CREATED, PENDING, invite -> workflowHandler.notifyInvitation(invite));
    }

    @Test
    public void rejectInvitation() {
        assertStateChangeOnWorkflowHandlerCall(PENDING, REJECTED, invite -> workflowHandler.rejectInvitation(invite, createRejection()),
                assessmentPanelApplicationInvite -> assertEquals("reason", assessmentPanelApplicationInvite.getRejection().getRejectionComment())
        );
    }

    private AssessmentReviewRejectOutcome createRejection() {
        return newAssessmentPanelApplicationInviteRejectOutcome().withRejectionComment("reason").build();
    }

    @Test
    public void acceptInvitation() {
        assertStateChangeOnWorkflowHandlerCall(PENDING, ACCEPTED, invite -> workflowHandler.acceptInvitation(invite));
    }

    @Test
    public void markConflictOfInterest() {
        assertStateChangeOnWorkflowHandlerCall(ACCEPTED, CONFLICT_OF_INTEREST, invite -> workflowHandler.markConflictOfInterest(invite));
    }

    @Test
    public void unmarkConflictOfInterest() {
        assertStateChangeOnWorkflowHandlerCall(CONFLICT_OF_INTEREST, ACCEPTED, invite -> workflowHandler.unmarkConflictOfInterest(invite));
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<AssessmentReviewWorkflowHandler> getWorkflowHandlerType() {
        return AssessmentReviewWorkflowHandler.class;
    }

    @Override
    protected Class<AssessmentReviewRepository> getProcessRepositoryType() {
        return AssessmentReviewRepository.class;
    }

    private ActivityType getActivityType() {
        return ACTIVITY_TYPE;
    }

    private AssessmentReviewRepository getRepositoryMock() {
        return repositoryMock;
    }


    private AssessmentReview buildWorkflowProcessWithInitialState(AssessmentReviewState initialState) {
        return newAssessmentReview().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentReviewState initialState, AssessmentReviewState expectedState, Function<AssessmentReview, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialState, expectedState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(AssessmentReviewState initialState, AssessmentReviewState expectedState, Function<AssessmentReview, Boolean> workflowHandlerMethod, Consumer<AssessmentReview> additionalVerifications) {
        AssessmentReview workflowProcess = buildWorkflowProcessWithInitialState(initialState);
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