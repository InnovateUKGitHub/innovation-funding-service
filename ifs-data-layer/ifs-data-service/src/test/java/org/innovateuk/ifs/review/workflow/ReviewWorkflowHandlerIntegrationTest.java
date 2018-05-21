package org.innovateuk.ifs.review.workflow;

import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.repository.ReviewRepository;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.review.workflow.configuration.ReviewWorkflowHandler;
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

import static org.innovateuk.ifs.review.builder.ReviewBuilder.newReview;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeBuilder.newReviewRejectOutcome;
import static org.innovateuk.ifs.review.resource.ReviewState.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class ReviewWorkflowHandlerIntegrationTest
        extends BaseWorkflowHandlerIntegrationTest<ReviewWorkflowHandler, ReviewRepository, TestableTransitionWorkflowAction> {

    @Autowired
    private ReviewWorkflowHandler workflowHandler;

    private ReviewRepository repositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        repositoryMock = (ReviewRepository) mockSupplier.apply(ReviewRepository.class);
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
                assessmentPanelApplicationInvite -> assertEquals("reason", assessmentPanelApplicationInvite.getRejection().getRejectReason())
        );
    }

    private ReviewRejectOutcome createRejection() {
        return newReviewRejectOutcome().withRejectionComment("reason").build();
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

    @Test
    public void withdraw_created() {
        assertStateChangeOnWorkflowHandlerCall(CREATED, WITHDRAWN, invite -> workflowHandler.withdraw(invite));
    }

    @Test
    public void withdraw_pending() {
        assertStateChangeOnWorkflowHandlerCall(PENDING, WITHDRAWN, invite -> workflowHandler.withdraw(invite));
    }

    @Test
    public void withdraw_rejected() {
        assertStateChangeOnWorkflowHandlerCall(REJECTED, WITHDRAWN, invite -> workflowHandler.withdraw(invite));
    }

    @Test
    public void withdraw_accepted() {
        assertStateChangeOnWorkflowHandlerCall(ACCEPTED, WITHDRAWN, invite -> workflowHandler.withdraw(invite));
    }

    @Test
    public void withdraw_conflict_of_interest() {
        assertStateChangeOnWorkflowHandlerCall(CONFLICT_OF_INTEREST, WITHDRAWN, invite -> workflowHandler.withdraw(invite));
    }

    @Override
    protected Class<TestableTransitionWorkflowAction> getBaseActionType() {
        return TestableTransitionWorkflowAction.class;
    }

    @Override
    protected Class<ReviewWorkflowHandler> getWorkflowHandlerType() {
        return ReviewWorkflowHandler.class;
    }

    @Override
    protected Class<ReviewRepository> getProcessRepositoryType() {
        return ReviewRepository.class;
    }

    private ReviewRepository getRepositoryMock() {
        return repositoryMock;
    }


    private Review buildWorkflowProcessWithInitialState(ReviewState initialState) {
        return newReview().withState(initialState).build();
    }

    private void assertStateChangeOnWorkflowHandlerCall(ReviewState initialState, ReviewState expectedState, Function<Review, Boolean> workflowHandlerMethod) {
        assertStateChangeOnWorkflowHandlerCall(initialState, expectedState, workflowHandlerMethod, null);
    }

    private void assertStateChangeOnWorkflowHandlerCall(ReviewState initialState, ReviewState expectedState, Function<Review, Boolean> workflowHandlerMethod, Consumer<Review> additionalVerifications) {
        Review workflowProcess = buildWorkflowProcessWithInitialState(initialState);
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