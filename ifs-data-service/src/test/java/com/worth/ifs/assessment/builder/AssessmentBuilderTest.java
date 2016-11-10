package com.worth.ifs.assessment.builder;

import com.worth.ifs.assessment.domain.Assessment;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.user.domain.ProcessRole;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import com.worth.ifs.workflow.resource.ProcessEvent;
import com.worth.ifs.workflow.resource.ProcessStates;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static com.worth.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static com.worth.ifs.assessment.builder.ProcessOutcomeBuilder.newProcessOutcome;
import static com.worth.ifs.assessment.resource.AssessmentStates.READY_TO_SUBMIT;
import static com.worth.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static com.worth.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;

public class AssessmentBuilderTest {

    @Test
    public void buildOne() throws Exception {
        final Long expectedId = 1L;
        final ProcessEvent expectedEvent = ProcessEvent.ASSESSMENT;
        final ProcessStates expectedStatus = AssessmentStates.OPEN;
        final Calendar expectedLastModifiedDate = GregorianCalendar.getInstance();
        final LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        final LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        final List<ProcessOutcome> expectedProcessOutcomes = newProcessOutcome().build(1);
        final ProcessRole expectedProcessRole = newProcessRole().build();

        final Assessment assessment = newAssessment()
                .withId(expectedId)
                .withProcessEvent(expectedEvent)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState()))
                .withLastModifiedDate(expectedLastModifiedDate)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withProcessOutcome(expectedProcessOutcomes)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, assessment.getId());
        assertEquals(expectedEvent.name(), assessment.getProcessEvent());
        assertEquals(expectedStatus, assessment.getActivityState());
        assertEquals(expectedLastModifiedDate, assessment.getLastModified());
        assertEquals(expectedStartDate, assessment.getStartDate());
        assertEquals(expectedEndDate, assessment.getEndDate());
        assertEquals(expectedProcessOutcomes, assessment.getProcessOutcomes());
        assertEquals(expectedProcessRole, assessment.getParticipant());
    }

    @Test
    public void buildMany() {
        final Long[] expectedIds = { 1L, 2L };
        final ProcessEvent[] expectedEvents = { ProcessEvent.ASSESSMENT, ProcessEvent.ANOTHER_ONE };
        final ProcessStates[] expectedStatuss = { AssessmentStates.OPEN, READY_TO_SUBMIT};
        final Calendar[] expectedLastModifiedDates = { GregorianCalendar.getInstance(), GregorianCalendar.getInstance() };
        final LocalDate[] expectedStartDates = { LocalDate.now().minusDays(2), LocalDate.now().minusDays(3) };
        final LocalDate[] expectedEndDates = { LocalDate.now().minusDays(1), LocalDate.now().minusDays(2) };
        final List<ProcessOutcome> expectedProcessOutcomes1 = newProcessOutcome().build(1);
        final List<ProcessOutcome> expectedProcessOutcomes2 = newProcessOutcome().build(1);
        final ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        final List<Assessment> assessments = newAssessment()
                .withId(expectedIds)
                .withProcessEvent(expectedEvents)
                .withActivityState(
                        new ActivityState(APPLICATION_ASSESSMENT, AssessmentStates.OPEN.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, READY_TO_SUBMIT.getBackingState()))
                .withLastModifiedDate(expectedLastModifiedDates)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withProcessOutcome(expectedProcessOutcomes1, expectedProcessOutcomes2)
                .withParticipant(expectedProcessRoles)
                .build(2);

        final Assessment first = assessments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedEvents[0].name(), first.getProcessEvent());
        assertEquals(expectedStatuss[0], first.getActivityState());
        assertEquals(expectedLastModifiedDates[0], first.getLastModified());
        assertEquals(expectedStartDates[0], first.getStartDate());
        assertEquals(expectedEndDates[0], first.getEndDate());
        assertEquals(expectedProcessOutcomes1, first.getProcessOutcomes());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        final Assessment second = assessments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedEvents[1].name(), second.getProcessEvent());
        assertEquals(expectedStatuss[1], second.getActivityState());
        assertEquals(expectedLastModifiedDates[1], second.getLastModified());
        assertEquals(expectedStartDates[1], second.getStartDate());
        assertEquals(expectedEndDates[1], second.getEndDate());
        assertEquals(expectedProcessOutcomes2, second.getProcessOutcomes());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }

}