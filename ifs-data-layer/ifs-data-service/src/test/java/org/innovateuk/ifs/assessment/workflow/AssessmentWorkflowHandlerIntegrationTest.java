package org.innovateuk.ifs.assessment.workflow;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.workflow.actions.BaseAssessmentAction;
import org.innovateuk.ifs.assessment.workflow.configuration.AssessmentWorkflowHandler;
import org.innovateuk.ifs.user.repository.ProcessRoleRepository;
import org.innovateuk.ifs.workflow.BaseWorkflowHandlerIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.time.ZonedDateTime.now;
import static org.innovateuk.ifs.LambdaMatcher.createLambdaMatcher;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue.CONFLICT_OF_INTEREST;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
public class AssessmentWorkflowHandlerIntegrationTest extends BaseWorkflowHandlerIntegrationTest<AssessmentWorkflowHandler, AssessmentRepository, BaseAssessmentAction> {

    @Autowired
    private AssessmentWorkflowHandler assessmentWorkflowHandler;

    private AssessmentRepository assessmentRepositoryMock;
    private ProcessRoleRepository processRoleRepositoryMock;

    @Override
    protected void collectMocks(Function<Class<? extends Repository>, Repository> mockSupplier) {
        assessmentRepositoryMock = (AssessmentRepository) mockSupplier.apply(AssessmentRepository.class);
        processRoleRepositoryMock = (ProcessRoleRepository) mockSupplier.apply(ProcessRoleRepository.class);
    }


    @Override
    protected List<Class<? extends Repository>> getRepositoriesToMock() {
        List<Class<? extends Repository>> repositories = new ArrayList<>(super.getRepositoriesToMock());
        repositories.add(ProcessRoleRepository.class);
        return repositories;
    }

    @Test
    public void notify_createdToPending() {
        assertWorkflowStateChange((assessment -> assessmentWorkflowHandler.notify(assessment)), setupIncompleteAssessment(CREATED), PENDING);
    }

    @Test
    public void withdrawAssessment_createdToWithdrawn() {
        assertWorkflowStateChangeForWithdrawnFromCreated((assessment -> assessmentWorkflowHandler.withdraw(assessment)), setupIncompleteAssessment(CREATED));
    }

    @Test
    public void rejectInvitation_pendingToRejected() {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(PENDING));
    }

    @Test
    public void acceptInvitation_pendingToAccepted() {
        assertWorkflowStateChange((assessment) -> assessmentWorkflowHandler.acceptInvitation(assessment), setupIncompleteAssessment(PENDING), ACCEPTED);
    }

    @Test
    public void withdrawAssessment_pendingToWithdrawn() {
        assertWorkflowStateChange(assessment -> assessmentWorkflowHandler.withdraw(assessment), setupIncompleteAssessment(PENDING), WITHDRAWN);
    }

    @Test
    public void rejectInvitation_acceptedToRejected() {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(ACCEPTED));
    }

    @Test
    public void withdrawAssessment_acceptedToWithdrawn() {
        assertWorkflowStateChange(assessment -> assessmentWorkflowHandler.withdraw(assessment), setupIncompleteAssessment(ACCEPTED), WITHDRAWN);
    }

    @Test
    public void feedback_acceptedToOpen() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void feedback_acceptedToReadyToSubmit() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_acceptedToOpen() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(ACCEPTED), OPEN);
    }

    @Test
    public void fundingDecision_acceptedToReadyToSubmit() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(ACCEPTED), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_openToRejected() {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupIncompleteAssessment(OPEN));
    }

    @Test
    public void withdrawAssessment_openToWithdrawn() {
        assertWorkflowStateChange(assessment -> assessmentWorkflowHandler.withdraw(assessment), setupIncompleteAssessment(OPEN), WITHDRAWN);
    }

    @Test
    public void feedback_openToOpen() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void feedback_openToReadyToSubmit() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_openToOpen() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(OPEN), OPEN);
    }

    @Test
    public void fundingDecision_openToReadyToSubmit() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(OPEN), READY_TO_SUBMIT);
    }

    @Test
    public void rejectInvitation_readyToSubmitToRejected() {
        assertWorkflowStateChangeForRejection((assessment) -> assessmentWorkflowHandler.rejectInvitation(assessment, createRejection()), setupCompleteAssessment(READY_TO_SUBMIT));
    }

    @Test
    public void withdrawAssessment_readyToSubmitToWithdrawn() {
        assertWorkflowStateChange(assessment -> assessmentWorkflowHandler.withdraw(assessment), setupIncompleteAssessment(READY_TO_SUBMIT), WITHDRAWN);
    }

    @Test
    public void feedback_readyToSubmitToOpen() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void feedback_readyToSubmitToReadyToSubmit() {
        assertWorkflowStateChangeForFeedback((assessment) -> assessmentWorkflowHandler.feedback(assessment), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void fundingDecision_readyToSubmitToOpen() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupIncompleteAssessment(READY_TO_SUBMIT), OPEN);
    }

    @Test
    public void fundingDecision_readyToSubmitToReadyToSubmit() {
        assertWorkflowStateChangeForFundingDecision((assessment) -> assessmentWorkflowHandler.fundingDecision(assessment, createFundingDecision()), setupCompleteAssessment(READY_TO_SUBMIT), READY_TO_SUBMIT);
    }

    @Test
    public void submit_readyToSubmitToSubmitted() {
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
    public void submit_readyToSubmitToSubmittedWhenNotInAssessmentPeriod() {
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
        assertEquals(READY_TO_SUBMIT, assessment.getProcessState());
    }

    private AssessmentFundingDecisionOutcome createFundingDecision() {
        return newAssessmentFundingDecisionOutcome()
                .withFundingConfirmation(true)
                .withComment("comment")
                .withFeedback("feedback")
                .build();
    }

    private AssessmentRejectOutcome createRejection() {
        return newAssessmentRejectOutcome()
                .withRejectReason(CONFLICT_OF_INTEREST)
                .withRejectComment("comment")
                .build();
    }

    private void assertWorkflowStateChangeForFeedback(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentState expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, (assessment) ->
                verify(assessmentRepositoryMock).isFeedbackComplete(assessment.getId()));
    }

    private void assertWorkflowStateChangeForFundingDecision(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentState expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, (assessment) -> {
            verify(assessmentRepositoryMock).isFeedbackComplete(assessment.getId());
            AssessmentFundingDecisionOutcome fundingDecision = assessment.getFundingDecision();
            assertNotNull(fundingDecision);
            assertTrue(fundingDecision.isFundingConfirmation());
            assertEquals("comment", fundingDecision.getComment());
            assertEquals("feedback", fundingDecision.getFeedback());
        });
    }

    private void assertWorkflowStateChangeForRejection(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, REJECTED, (assessment) -> {
            AssessmentRejectOutcome rejection = assessment.getRejection();
            assertNotNull(rejection);
            assertEquals(CONFLICT_OF_INTEREST, rejection.getRejectReason());
            assertEquals("comment", rejection.getRejectComment());
        });
    }

    private void assertWorkflowStateChangeForWithdrawnFromCreated(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier) {
        Assessment assessment = assessmentSupplier.get();

        // now call the method under test
        assertTrue(handlerMethod.apply(assessment));

        verify(assessmentRepositoryMock).delete(assessment);
        verify(processRoleRepositoryMock).delete(assessment.getParticipant());
        verifyNoMoreInteractionsWithMocks();
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentState expectedState) {
        assertWorkflowStateChange(handlerMethod, assessmentSupplier, expectedState, null);
    }

    private void assertWorkflowStateChange(Function<Assessment, Boolean> handlerMethod, Supplier<Assessment> assessmentSupplier, AssessmentState expectedState, Consumer<Assessment> additionalVerifications) {
        Assessment assessment = assessmentSupplier.get();

        // now call the method under test
        assertTrue(handlerMethod.apply(assessment));

        verify(assessmentRepositoryMock).save(createAssessmentExpectations(assessment, expectedState));

        if (additionalVerifications != null) {
            additionalVerifications.accept(assessment);
        }

        verifyNoMoreInteractionsWithMocks();
    }

    private Assessment createAssessmentExpectations(Assessment assessment, AssessmentState expectedState) {
        return createLambdaMatcher(actual -> {
            assertEquals(assessment, actual);
            // This is the only sure way of checking that your state transition happened!
            assertTrue(actual.isInState(expectedState));
        });
    }

    private Supplier<Assessment> setupIncompleteAssessment(AssessmentState initialState) {
        return () -> newAssessment()
                .withProcessState(initialState)
                .build();
    }

    private Supplier<Assessment> setupCompleteAssessment(AssessmentState initialState) {
        return () -> {
            Assessment assessment = newAssessment()
                    .withProcessState(initialState)
                    .withFundingDecision(newAssessmentFundingDecisionOutcome().build())
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