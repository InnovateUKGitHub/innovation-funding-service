package org.innovateuk.ifs.assessment.workflow.guards;

import org.innovateuk.ifs.BaseUnitTestMocksTest;
import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.resource.AssessmentEvent;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.statemachine.StateContext;

import java.util.Arrays;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodBuilder.newAssessmentPeriod;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CompetitionInAssessmentGuardTest extends BaseUnitTestMocksTest {

    @InjectMocks
    private CompetitionInAssessmentGuard competitionInAssessmentGuard = new CompetitionInAssessmentGuard();

    @Test
    public void evaluate_assessmentDatesNotSet() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartsInFuture() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                        .withAssessorsNotifiedDate(now().plusDays(10L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartsAndClosesInFuture() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                        .withAssessorsNotifiedDate(now().plusDays(10L))
                        .withAssessmentClosedDate(now().plusDays(20L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartedAndClosed() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                        .withAssessorsNotifiedDate(now().minusDays(10L))
                        .withAssessmentClosedDate(now().minusDays(1L))
                        .build()
        );

        assertFalse(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartedButWillCloseInFuture() {
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
    public void evaluate_assessmentStartedButCloseDateNotSet() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
                        .withCompetitionStatus(CLOSED)
                        .withAssessorsNotifiedDate(now().minusDays(10L))
                        .build()
        );

        assertTrue(competitionInAssessmentGuard.evaluate(setupContext(assessment)));
    }

    @Test
    public void evaluate_assessmentStartDateNotSetButClosesInFuture() {
        Assessment assessment = buildAssessment(
                newCompetition()
                        .withCompetitionStatus(CLOSED)
                        .withAssessmentPeriods(asList(newAssessmentPeriod().build()))
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

    private StateContext<AssessmentState, AssessmentEvent> setupContext(Assessment assessment) {
        StateContext<AssessmentState, AssessmentEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("target")).thenReturn(assessment);
        return context;
    }
}
