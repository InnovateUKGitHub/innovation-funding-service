package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentOutcomes.FUNDING_DECISION;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessmentCompleteGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private AssessmentCompleteGuard assessmentCompleteGuard = new AssessmentCompleteGuard();

    @Test
    public void evaluate_feedbackIncompleteAndFundingDecisionAbsent() throws Exception {
        Assessment assessment = setupAssessmentWithoutFundingDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(false);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackIncompleteAndFundingDecisionPresent() throws Exception {
        Assessment assessment = setUpAssessmentWithFundingDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(false);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackCompleteAndFundingDecisionAbsent() throws Exception {
        Assessment assessment = setupAssessmentWithoutFundingDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(true);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackCompleteAndFundingDecisionPresent() throws Exception {
        Assessment assessment = setUpAssessmentWithFundingDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(true);
        assertTrue(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    private Assessment setupAssessmentWithoutFundingDecision() {
        return newAssessment().build();
    }

    private Assessment setUpAssessmentWithFundingDecision() {
        return newAssessment()
                .withProcessOutcome(newProcessOutcome()
                        .withOutcomeType(FUNDING_DECISION.getType())
                        .build(1))
                .build();
    }

    private StateContext<AssessmentStates, AssessmentOutcomes> setupContext(Assessment assessment) {
        StateContext<AssessmentStates, AssessmentOutcomes> context = mock(StateContext.class);
        when(context.getMessageHeader("assessment")).thenReturn(assessment);
        return context;
    }
}