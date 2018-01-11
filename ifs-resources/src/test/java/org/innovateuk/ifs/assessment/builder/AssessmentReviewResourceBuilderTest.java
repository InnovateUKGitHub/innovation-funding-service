package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewState;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.junit.Assert.assertEquals;

public class AssessmentReviewResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        AssessmentReviewState expectedStatus = AssessmentReviewState.PENDING;
        LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        AssessmentReviewRejectOutcomeResource expectedRejection = newAssessmentReviewRejectOutcomeResource().build();
        Long expectedProcessRole = 2L;
        Long expectedApplication = 3L;
        String expectedApplicationName = "name";
        Long expectedCompetition = 4L;

        AssessmentReviewResource assessment = newAssessmentReviewResource()
                .withId(expectedId)
                .withActivityState(expectedStatus)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withRejection(expectedRejection)
                .withProcessRole(expectedProcessRole)
                .withApplication(expectedApplication)
                .withApplicationName(expectedApplicationName)
                .withCompetition(expectedCompetition)
                .build();

        assertEquals(expectedId, assessment.getId());
        assertEquals(expectedStatus, assessment.getAssessmentReviewState());
        assertEquals(expectedStartDate, assessment.getStartDate());
        assertEquals(expectedEndDate, assessment.getEndDate());
        assertEquals(expectedRejection, assessment.getRejection());
        assertEquals(expectedProcessRole, assessment.getProcessRole());
        assertEquals(expectedApplication, assessment.getApplication());
        assertEquals(expectedApplicationName, assessment.getApplicationName());
        assertEquals(expectedCompetition, assessment.getCompetition());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        AssessmentReviewState[] expectedStatuses = {AssessmentReviewState.PENDING, AssessmentReviewState.ACCEPTED};
        LocalDate[] expectedStartDates = {LocalDate.now().minusDays(2), LocalDate.now().minusDays(3)};
        LocalDate[] expectedEndDates = {LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)};
        AssessmentReviewRejectOutcomeResource[] expectedRejections = newAssessmentReviewRejectOutcomeResource()
                .buildArray(2, AssessmentReviewRejectOutcomeResource.class);
        Long[] expectedProcessRoles = {1L, 2L};
        Long[] expectedApplications = {3L, 4L};
        String[] expectedApplicationNames = {"name 1", "name 2"};
        Long[] expectedCompetitions = {5L, 6L};

        List<AssessmentReviewResource> assessments = newAssessmentReviewResource()
                .withId(expectedIds)
                .withActivityState(expectedStatuses)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withRejection(expectedRejections)
                .withProcessRole(expectedProcessRoles)
                .withApplication(expectedApplications)
                .withApplicationName(expectedApplicationNames)
                .withCompetition(expectedCompetitions)
                .build(2);

        AssessmentReviewResource first = assessments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getAssessmentReviewState());
        assertEquals(expectedStartDates[0], first.getStartDate());
        assertEquals(expectedEndDates[0], first.getEndDate());
        assertEquals(expectedRejections[0], first.getRejection());
        assertEquals(expectedProcessRoles[0], first.getProcessRole());
        assertEquals(expectedApplications[0], first.getApplication());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedCompetitions[0], first.getCompetition());

        AssessmentReviewResource second = assessments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getAssessmentReviewState());
        assertEquals(expectedStartDates[1], second.getStartDate());
        assertEquals(expectedEndDates[1], second.getEndDate());
        assertEquals(expectedRejections[1], second.getRejection());
        assertEquals(expectedProcessRoles[1], second.getProcessRole());
        assertEquals(expectedApplications[1], second.getApplication());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedCompetitions[1], second.getCompetition());
    }
}
