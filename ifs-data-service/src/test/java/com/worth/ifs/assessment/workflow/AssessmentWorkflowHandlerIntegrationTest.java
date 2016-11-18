package com.worth.ifs.assessment.workflow;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.ApplicationRejectionResource;
import com.worth.ifs.assessment.resource.AssessmentFundingDecisionResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.assessment.workflow.actions.BaseAssessmentAction;
import com.worth.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import com.worth.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.worth.ifs.LambdaMatcher.createLambdaMatcher;
import static com.worth.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentOutcomes.FUNDING_DECISION;
import static com.worth.ifs.assessment.resource.AssessmentOutcomes.REJECT;
import static com.worth.ifs.assessment.resource.AssessmentStates.*;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@Transactional
public class AssessmentWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<AssessmentWorkflowHandler, AssessmentRepository, BaseAssessmentAction> {

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    private ActivityStateRepository activityStateRepositoryMock;
    private AssessmentRepository assessmentRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        activityStateRepositoryMock = (ActivityStateRepository) mockSupplier.apply(ActivityStateRepository.class);
        assessmentRepositoryMock = (AssessmentRepository) mockSupplier.apply(AssessmentRepository.class);
    }

    @Test
    public void rejectInvitation_pendingToRejected() throws Exception {
        assertWorkflowStateChangeWithRejectionOutcome((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(PENDING));
    }

    @Test
    public void acceptInvitation_pendingToAccepted() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.acceptInvitation(assessment), setupIncompleteAssessment(PENDING), ACCEPTED);
    }

    @Test
    public void rejectInvitation_acceptedToRejected() throws Exception {
        assertWorkflowStateChangeWithRejectionOutcome((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(ACCEPTED));
    }

    @Test
    public void feedback_acceptedToOpen() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void feedback_acceptedToReadyToSubmit() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_acceptedToOpen() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void fundingDecision_acceptedToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_openToRejected() throws Exception {
        assertWorkflowStateChangeWithRejectionOutcome((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(OPEN));
    }

    @Test
    public void feedback_openToOpen() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void feedback_openToReadyToSubmit() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_openToOpen() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void fundingDecision_openToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_readyToSubmitToRejected() throws Exception {
        assertWorkflowStateChangeWithRejectionOutcome((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupCompleteAssessment(READY_TO_SUBMIT));
    }

    @Test
    public void feedback_readyToSubmitToOpen() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void feedback_readyToSubmitToReadyToSubmit() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_readyToSubmitToOpen() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void fundingDecision_readyToSubmitToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeWithFundingDecisionOutcome((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void submit_readyToSubmitToSubmitted() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.submit(assessment), setupCompleteAssessment(READY_TO_SUBMIT), SUBMITTED);
    }

    private AssessmentFundingDecisionResource createFundingDecision() {
        return newAssessmentFundingDecisionResource()
                .withFundingConfirmation(TRUE)
                .withComment("comment")
                .withFeedback("feedback")
                .build();
    }

    private ApplicationRejectionResource createRejection() {
        return newApplicationRejectionResource()
                .withRejectReason("reason")
                .withRejectComment("comment")
                .build();
    }

    private void assertWorkflowStateChangeWithFundingDecisionOutcome(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, (assessment) -> {
            Optional<ProcessOutcome> fundingDecision = assessment.getLastOutcome(FUNDING_DECISION);
            assertTrue(fundingDecision.isPresent());
            assertEquals("yes", fundingDecision.get().getOutcome());
            assertEquals("comment", fundingDecision.get().getComment());
            assertEquals("feedback", fundingDecision.get().getDescription());
        });
    }

    private void assertWorkflowStateChangeWithRejectionOutcome(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, REJECTED, (assessment) -> {
            Optional<ProcessOutcome> rejection = assessment.getLastOutcome(REJECT);
            assertTrue(rejection.isPresent());
            assertEquals("comment", rejection.get().getComment());
            assertEquals("reason", rejection.get().getDescription());
        });
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, null);
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState, Consumer<Assessment> additionalVerifications) {
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedState.getBackingState())).thenReturn(new ActivityState(APPLICATION_ASSESSMENT, expectedState.getBackingState()));

        Assessment assessment = assessmentSupplier.get();

        // now call the method under test
        assertTrue(handlerMethod.apply(assessment));

        //verify(activityStateRepositoryMock, times(1)).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedState.getBackingState());

        //verify(assessmentRepositoryMock).save(createAssessmentExpectations(assessment.getId()));

        assertEquals(expectedState, assessment.getActivityState());
        if (additionalVerifications != null) {
            additionalVerifications.accept(assessment);
        }

        //verifyNoMoreInteractionsWithMocks();
    }

    private Assessment createAssessmentExpectations(Long assessmentId, AssessmentStates expectedState) {
        return createLambdaMatcher(assessment -> {
            assertEquals(assessmentId, assessment.getId());
            assertTrue(assessment.isInState(expectedState));
        });
    }

    private Supplier<Assessment> setupIncompleteAssessment(AssessmentStates initialState) {
        return () -> newAssessment()
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, initialState.getBackingState()))
                .withProcessOutcome(new ArrayList<>())
                .build();
    }

    private Supplier<Assessment> setupCompleteAssessment(AssessmentStates initialState) {
        return () -> {
            Assessment assessment = newAssessment()
                    .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, initialState.getBackingState()))
                    .withProcessOutcome(newProcessOutcome()
                            .withOutcomeType(FUNDING_DECISION.getType())
                            .build(1))
                    .build();
            when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(TRUE);
            return assessment;
        };
    }

    @Override
    protected Class<BaseAssessmentAction> getBaseActionType() {
        return BaseAssessmentAction.class;
    }

    @Override
    protected Class<AssessmentWorkflowHandler> getWorkflowHandlerType() {
        return AssessmentWorkflowHandler.class;
    }

    @Override
    protected Class<AssessmentRepository> getProcessRepositoryType() {
        return AssessmentRepository.class;
    }
}
