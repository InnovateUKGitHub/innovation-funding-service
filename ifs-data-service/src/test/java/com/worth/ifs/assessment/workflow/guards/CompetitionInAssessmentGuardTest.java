package com.worth.ifs.assessment.workflow.guards;

import com.worth.ifs.BaseUnitTestMocksTest;
import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import static com.worth.ifs.application.builder.ApplicationBuilder.newApplication;
import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static com.worth.ifs.competition.resource.CompetitionStatus.CLOSED;
import static java.time.LocalDateTime.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompetitionInAssessmentGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionInAssessmentGuard competitionInAssessmentGuard = new CompetitionInAssessmentGuard();

    @Test
    public void evaluate_assessmentDatesNotSet() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartsInFuture() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().plusDays(10L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartsAndClosesInFuture() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().plusDays(10L))
                        .withAssessmentClosedDate(now().plusDays(20L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartedAndClosed() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().minusDays(10L))
                        .withAssessmentClosedDate(now().minusDays(1L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartedButWillCloseInFuture() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().minusDays(10L))
                        .withAssessmentClosedDate(now().plusDays(10L))
                        .build()
        );

        assertTrue(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartedButCloseDateNotSet() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().minusDays(10L))
                        .build()
        );

        assertTrue(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartDateNotSetButClosesInFuture() throws Exception {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessmentClosedDate(now().plusDays(10L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    private Assessment buildAssessment(Competition competition) {
        return newAssessment()
                .withApplication(
                        newApplication()
                                .withCompetition(competition)
                                .build()
                )
                .build();
    }

    private StateContext<AssessmentStates, AssessmentOutcomes> setupContext(Assessment assessment) {
        StateContext<AssessmentStates, AssessmentOutcomes> context = mock(StateContext.class);
        when(context.getMessageHeader("assessment")).thenReturn(assessment);
        return context;
    }
}
