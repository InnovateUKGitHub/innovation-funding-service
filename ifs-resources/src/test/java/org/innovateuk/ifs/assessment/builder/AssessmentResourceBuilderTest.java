package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;
import org.innovateuk.ifs.workflow.resource.ProcessStates;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.READY_TO_SUBMIT;
import static org.junit.Assert.assertEquals;

public class AssessmentResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessEvent expectedEvent = ProcessEvent.ASSESSMENT;
        ProcessStates expectedStatus = OPEN;
        Calendar expectedLastModifiedDate = GregorianCalendar.getInstance();
        LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        List<Long> expectedProcessOutcomes = asList(1L);
        Long expectedProcessRole = 2L;
        Long expectedApplication = 3L;
        String expectedApplicationName = "name";
        Long expectedCompetition = 4L;

        AssessmentResource assessment = newAssessmentResource()
                .withId(expectedId)
                .withProcessEvent(expectedEvent)
                .withActivityState(OPEN)
                .withLastModifiedDate(expectedLastModifiedDate)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withProcessOutcome(expectedProcessOutcomes)
                .withProcessRole(expectedProcessRole)
                .withApplication(expectedApplication)
                .withApplicationName(expectedApplicationName)
                .withCompetition(expectedCompetition)
                .build();

        assertEquals(expectedId, assessment.getId());
        assertEquals(expectedEvent.name(), assessment.getEvent());
        assertEquals(expectedStatus, assessment.getAssessmentState());
        assertEquals(expectedLastModifiedDate, assessment.getLastModified());
        assertEquals(expectedStartDate, assessment.getStartDate());
        assertEquals(expectedEndDate, assessment.getEndDate());
        assertEquals(expectedProcessOutcomes, assessment.getProcessOutcomes());
        assertEquals(expectedProcessRole, assessment.getProcessRole());
        assertEquals(expectedApplication, assessment.getApplication());
        assertEquals(expectedApplicationName, assessment.getApplicationName());
        assertEquals(expectedCompetition, assessment.getCompetition());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessEvent[] expectedEvents = {ProcessEvent.ASSESSMENT, ProcessEvent.ANOTHER_ONE};
        ProcessStates[] expectedStatuss = {OPEN, READY_TO_SUBMIT};
        Calendar[] expectedLastModifiedDates = {GregorianCalendar.getInstance(), GregorianCalendar.getInstance()};
        LocalDate[] expectedStartDates = {LocalDate.now().minusDays(2), LocalDate.now().minusDays(3)};
        LocalDate[] expectedEndDates = {LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)};
        List<Long>[] expectedProcessOutcomes = new List[]{asList(1L), asList(2L)};
        Long[] expectedProcessRoles = {1L, 2L};
        Long[] expectedApplications = {3L, 4L};
        String[] expectedApplicationNames = {"name 1", "name 2"};
        Long[] expectedCompetitions = {5L, 6L};

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(expectedIds)
                .withProcessEvent(expectedEvents)
                .withActivityState(OPEN, READY_TO_SUBMIT)
                .withLastModifiedDate(expectedLastModifiedDates)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withProcessOutcome(expectedProcessOutcomes)
                .withProcessRole(expectedProcessRoles)
                .withApplication(expectedApplications)
                .withApplicationName(expectedApplicationNames)
                .withCompetition(expectedCompetitions)
                .build(2);

        AssessmentResource first = assessments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedEvents[0].name(), first.getEvent());
        assertEquals(expectedStatuss[0], first.getAssessmentState());
        assertEquals(expectedLastModifiedDates[0], first.getLastModified());
        assertEquals(expectedStartDates[0], first.getStartDate());
        assertEquals(expectedEndDates[0], first.getEndDate());
        assertEquals(expectedProcessOutcomes[0], first.getProcessOutcomes());
        assertEquals(expectedProcessRoles[0], first.getProcessRole());
        assertEquals(expectedApplications[0], first.getApplication());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedCompetitions[0], first.getCompetition());

        AssessmentResource second = assessments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedEvents[1].name(), second.getEvent());
        assertEquals(expectedStatuss[1], second.getAssessmentState());
        assertEquals(expectedLastModifiedDates[1], second.getLastModified());
        assertEquals(expectedStartDates[1], second.getStartDate());
        assertEquals(expectedEndDates[1], second.getEndDate());
        assertEquals(expectedProcessOutcomes[1], second.getProcessOutcomes());
        assertEquals(expectedProcessRoles[1], second.getProcessRole());
        assertEquals(expectedApplications[1], second.getApplication());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedCompetitions[1], second.getCompetition());
    }
}
