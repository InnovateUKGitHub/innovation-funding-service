package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewResource;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewResourceBuilder.newInterviewResource;
import static org.junit.Assert.assertEquals;

public class InterviewResourceBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        InterviewState expectedStatus = InterviewState.ASSIGNED;
        LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        Long expectedProcessRole = 2L;
        Long expectedApplication = 3L;
        String expectedApplicationName = "name";
        Long expectedCompetition = 4L;

        InterviewResource assessment = newInterviewResource()
                .withId(expectedId)
                .withActivityState(expectedStatus)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withProcessRole(expectedProcessRole)
                .withApplication(expectedApplication)
                .withApplicationName(expectedApplicationName)
                .withCompetition(expectedCompetition)
                .build();

        assertEquals(expectedId, assessment.getId());
        assertEquals(expectedStatus, assessment.getInterviewState());
        assertEquals(expectedStartDate, assessment.getStartDate());
        assertEquals(expectedEndDate, assessment.getEndDate());
        assertEquals(expectedProcessRole, assessment.getProcessRole());
        assertEquals(expectedApplication, assessment.getApplication());
        assertEquals(expectedApplicationName, assessment.getApplicationName());
        assertEquals(expectedCompetition, assessment.getCompetition());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        InterviewState[] expectedStatuses = {InterviewState.ASSIGNED, InterviewState.ASSIGNED};
        LocalDate[] expectedStartDates = {LocalDate.now().minusDays(2), LocalDate.now().minusDays(3)};
        LocalDate[] expectedEndDates = {LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)};
        Long[] expectedProcessRoles = {1L, 2L};
        Long[] expectedApplications = {3L, 4L};
        String[] expectedApplicationNames = {"name 1", "name 2"};
        Long[] expectedCompetitions = {5L, 6L};

        List<InterviewResource> assessments = newInterviewResource()
                .withId(expectedIds)
                .withActivityState(expectedStatuses)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withProcessRole(expectedProcessRoles)
                .withApplication(expectedApplications)
                .withApplicationName(expectedApplicationNames)
                .withCompetition(expectedCompetitions)
                .build(2);

        InterviewResource first = assessments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getInterviewState());
        assertEquals(expectedStartDates[0], first.getStartDate());
        assertEquals(expectedEndDates[0], first.getEndDate());
        assertEquals(expectedProcessRoles[0], first.getProcessRole());
        assertEquals(expectedApplications[0], first.getApplication());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedCompetitions[0], first.getCompetition());

        InterviewResource second = assessments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getInterviewState());
        assertEquals(expectedStartDates[1], second.getStartDate());
        assertEquals(expectedEndDates[1], second.getEndDate());
        assertEquals(expectedProcessRoles[1], second.getProcessRole());
        assertEquals(expectedApplications[1], second.getApplication());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedCompetitions[1], second.getCompetition());
    }
}