package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
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
                .withFundingDecision(newAssessmentFundingDecisionOutcome().build())
                .build();
    }

    private StateContext<AssessmentState, AssessmentEvent> setupContext(Assessment assessment) {
        StateContext<AssessmentState, AssessmentEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("target")).thenReturn(assessment);
        return context;
    }
}
