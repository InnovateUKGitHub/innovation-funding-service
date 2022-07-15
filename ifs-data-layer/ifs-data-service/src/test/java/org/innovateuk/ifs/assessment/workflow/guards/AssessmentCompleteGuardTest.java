package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.repository.AssessmentRepository;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentDecisionOutcomeBuilder.newAssessmentDecisionOutcome;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AssessmentCompleteGuardTest extends BaseUnitTestMocksTest {

    @Mock
    private AssessmentRepository assessmentRepositoryMock;

    @InjectMocks
    private AssessmentCompleteGuard assessmentCompleteGuard = new AssessmentCompleteGuard();

    @Test
    public void evaluate_feedbackIncompleteAndDecisionAbsent() throws Exception {
        Assessment assessment = setupAssessmentWithoutDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(false);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackIncompleteAndDecisionPresent() throws Exception {
        Assessment assessment = setUpAssessmentWithDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(false);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackCompleteAndDecisionAbsent() throws Exception {
        Assessment assessment = setupAssessmentWithoutDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(true);
        assertFalse(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    @Test
    public void evaluate_feedbackCompleteAndDecisionPresent() throws Exception {
        Assessment assessment = setUpAssessmentWithDecision();
        when(assessmentRepositoryMock.isFeedbackComplete(assessment.getId())).thenReturn(true);
        assertTrue(assessmentCompleteGuard.evaluate(setupContext(assessment)));
        verify(assessmentRepositoryMock, only()).isFeedbackComplete(assessment.getId());
    }

    private Assessment setupAssessmentWithoutDecision() {
        return newAssessment().build();
    }

    private Assessment setUpAssessmentWithDecision() {
        return newAssessment()
                .withDecision(newAssessmentDecisionOutcome().build())
                .build();
    }

    private StateContext<AssessmentState, AssessmentEvent> setupContext(Assessment assessment) {
        StateContext<AssessmentState, AssessmentEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("target")).thenReturn(assessment);
        return context;
    }
}
