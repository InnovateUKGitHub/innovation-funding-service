package org.innovateuk.ifs.assessment.workflow;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.ApplicationRejectionResource;
import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.workflow.actions.BaseAssessmentAction;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.domain.ProcessOutcome;
import org.innovateuk.ifs.workflow.repository.ActivityStateRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.ApplicationRejectionResourceBuilder.newApplicationRejectionResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionResourceBuilder.newAssessmentFundingDecisionResource;
import static org.innovateuk.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.FUNDING_DECISION;
import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.REJECT;
import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.WITHDRAW;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static java.lang.Boolean.TRUE;
import static java.time.LocalDateTime.now;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
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
    public void notify_createdToPending() throws Exception {
        assertWorkflowStateChange((assessment -> assessmentWorkflowHandler.notify(assessment)), setupIncompleteAssessment(CREATED), PENDING);
    }

    @Test
    public void rejectInvitation_pendingToRejected() throws Exception {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(PENDING));
    }

    @Test
    public void acceptInvitation_pendingToAccepted() throws Exception {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.acceptInvitation(assessment), setupIncompleteAssessment(PENDING), ACCEPTED);
    }

    @Test
    public void withdrawAssessment_pendingToWithdrawn() throws Exception {
        assertWorkflowStateChangeForWithdrawn((assessment -> assessmentWorkflowHandler.withdrawAssessment(assessment)), setupIncompleteAssessment(PENDING));
    }

    @Test
    public void rejectInvitation_acceptedToRejected() throws Exception {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(ACCEPTED));
    }

    @Test
    public void withdrawAssessment_acceptedToWithdrawn() throws Exception {
        assertWorkflowStateChangeForWithdrawn((assessment -> assessmentWorkflowHandler.withdrawAssessment(assessment)), setupIncompleteAssessment(ACCEPTED));
    }

    @Test
    public void feedback_acceptedToOpen() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void feedback_acceptedToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_acceptedToOpen() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void fundingDecision_acceptedToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_openToRejected() throws Exception {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(OPEN));
    }

    @Test
    public void withdrawAssessment_openToWithdrawn() throws Exception {
        assertWorkflowStateChangeForWithdrawn((assessment -> assessmentWorkflowHandler.withdrawAssessment(assessment)), setupIncompleteAssessment(OPEN));
    }

    @Test
    public void feedback_openToOpen() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void feedback_openToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_openToOpen() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void fundingDecision_openToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_readyToSubmitToRejected() throws Exception {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupCompleteAssessment(READY_TO_SUBMIT));
    }

    @Test
    public void withdrawAssessment_readyToSubmitToWithdrawn() throws Exception {
        assertWorkflowStateChangeForWithdrawn((assessment -> assessmentWorkflowHandler.withdrawAssessment(assessment)), setupCompleteAssessment(READY_TO_SUBMIT));

    }

    @Test
    public void feedback_readyToSubmitToOpen() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void feedback_readyToSubmitToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_readyToSubmitToOpen() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void fundingDecision_readyToSubmitToReadyToSubmit() throws Exception {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void submit_readyToSubmitToSubmitted() throws Exception {
        Supplier<Assessment> completeAssessment = () -> {
            Assessment assessment = setupCompleteAssessment(READY_TO_SUBMIT).get();
            assessment.setTarget(
                    newApplication()
                            .withCompetition(
                                    newCompetition()
                                            .withCompetitionStatus(CLOSED)
                                            .withAssessorsNotifiedDate(now().minusDays(10L))
                                            .withAssessmentClosedDate(now().plusDays(10L))
                                            .build()
                            )
                            .build()
            );

            return assessment;
        };

        assertWorkflowStateChange(assessment -> assessmentWorkflowHandler.submit(assessment), completeAssessment, SUBMITTED);
    }

    @Test
    public void submit_readyToSubmitToSubmittedWhenNotInAssessmentPeriod() throws Exception {
        Assessment assessment = setupCompleteAssessment(READY_TO_SUBMIT).get();
        assessment.setTarget(
                newApplication()
                        .withCompetition(
                                newCompetition()
                                        .withCompetitionStatus(CLOSED)
                                        .withAssessorsNotifiedDate(now().plusDays(10L))
                                        .withAssessmentClosedDate(now().plusDays(20L))
                                        .build()
                        )
                        .build()
        );

        // The boolean returned here is actually indicating if the transition
        // was accepted by the StateMachine or not (rather than if
        // it was a successful/rejected transition)
        assertTrue(assessmentWorkflowHandler.submit(assessment));
        assertEquals(READY_TO_SUBMIT, assessment.getActivityState());
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

    private void assertWorkflowStateChangeForFeedback(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, (assessment) -> {
            verify(assessmentRepositoryMock).isFeedbackComplete(assessment.getId());
        });
    }

    private void assertWorkflowStateChangeForFundingDecision(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, (assessment) -> {
            verify(assessmentRepositoryMock).isFeedbackComplete(assessment.getId());
            Optional<ProcessOutcome> fundingDecision = assessment.getLastOutcome(FUNDING_DECISION);
            assertTrue(fundingDecision.isPresent());
            assertEquals("yes", fundingDecision.get().getOutcome());
            assertEquals("comment", fundingDecision.get().getComment());
            assertEquals("feedback", fundingDecision.get().getDescription());
        });
    }

    private void assertWorkflowStateChangeForRejection(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, REJECTED, (assessment) -> {
            Optional<ProcessOutcome> rejection = assessment.getLastOutcome(REJECT);
            assertTrue(rejection.isPresent());
            assertEquals("comment", rejection.get().getComment());
            assertEquals("reason", rejection.get().getDescription());
        });
    }

    private void assertWorkflowStateChangeForWithdrawn(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, WITHDRAWN, (assessment)  -> {
            Optional<ProcessOutcome> withdrawn = assessment.getLastOutcome(WITHDRAW);
            assertTrue(withdrawn.isPresent());
        });
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, null);
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentStates expectedState, Consumer<Assessment> additionalVerifications) {
        when(activityStateRepositoryMock.findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedState.getBackingState()))
                .thenReturn(new ActivityState(APPLICATION_ASSESSMENT, expectedState.getBackingState()));

        Assessment assessment = assessmentSupplier.get();

        // now call the method under test
        assertTrue(handlerMethod.apply(assessment));

        verify(activityStateRepositoryMock).findOneByActivityTypeAndState(APPLICATION_ASSESSMENT, expectedState.getBackingState());
        verify(assessmentRepositoryMock).save(createAssessmentExpectations(assessment, expectedState));

        if (additionalVerifications != null) {
            additionalVerifications.accept(assessment);
        }

        verifyNoMoreInteractionsWithMocks();
    }

    private Assessment createAssessmentExpectations(Assessment assessment, AssessmentStates expectedState) {
        return createLambdaMatcher(actual -> {
            assertEquals(assessment, actual);
            // This is the only sure way of checking that your state transition happened!
            assertTrue(actual.isInState(expectedState));
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
            when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(true);
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
