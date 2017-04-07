package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.AssessmentFundingDecisionOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentResource;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.workflow.resource.ProcessEvent;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeResourceBuilder.newAssessmentFundingDecisionOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeResourceBuilder.newAssessmentRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentResourceBuilder.newAssessmentResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.OPEN;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.READY_TO_SUBMIT;
import static org.junit.Assert.*;

public class AssessmentResourceBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessEvent expectedEvent = ProcessEvent.ASSESSMENT;
        AssessmentStates expectedStatus = OPEN;
        ZonedDateTime expectedLastModifiedDate = ZonedDateTime.now();
        LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        AssessmentFundingDecisionOutcomeResource expectedFundingDecision = newAssessmentFundingDecisionOutcomeResource().build();
        AssessmentRejectOutcomeResource expectedRejection = newAssessmentRejectOutcomeResource().build();
        Long expectedProcessRole = 2L;
        Long expectedApplication = 3L;
        String expectedApplicationName = "name";
        Long expectedCompetition = 4L;

        AssessmentResource assessment = newAssessmentResource()
                .withId(expectedId)
                .withProcessEvent(expectedEvent)
                .withActivityState(expectedStatus)
                .withLastModifiedDate(expectedLastModifiedDate)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withFundingDecision(expectedFundingDecision)
                .withRejection(expectedRejection)
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
        assertEquals(expectedFundingDecision, assessment.getFundingDecision());
        assertEquals(expectedRejection, assessment.getRejection());
        assertEquals(expectedProcessRole, assessment.getProcessRole());
        assertEquals(expectedApplication, assessment.getApplication());
        assertEquals(expectedApplicationName, assessment.getApplicationName());
        assertEquals(expectedCompetition, assessment.getCompetition());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessEvent[] expectedEvents = {ProcessEvent.ASSESSMENT, ProcessEvent.ANOTHER_ONE};
        AssessmentStates[] expectedStatuss = {OPEN, READY_TO_SUBMIT};
        ZonedDateTime[] expectedLastModifiedDates = {ZonedDateTime.now(), ZonedDateTime.now()};
        LocalDate[] expectedStartDates = {LocalDate.now().minusDays(2), LocalDate.now().minusDays(3)};
        LocalDate[] expectedEndDates = {LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)};
        AssessmentFundingDecisionOutcomeResource[] expectedFundingDecisions = newAssessmentFundingDecisionOutcomeResource()
                .buildArray(2, AssessmentFundingDecisionOutcomeResource.class);
        AssessmentRejectOutcomeResource[] expectedRejections = newAssessmentRejectOutcomeResource()
                .buildArray(2, AssessmentRejectOutcomeResource.class);
        Long[] expectedProcessRoles = {1L, 2L};
        Long[] expectedApplications = {3L, 4L};
        String[] expectedApplicationNames = {"name 1", "name 2"};
        Long[] expectedCompetitions = {5L, 6L};

        List<AssessmentResource> assessments = newAssessmentResource()
                .withId(expectedIds)
                .withProcessEvent(expectedEvents)
                .withActivityState(expectedStatuss)
                .withLastModifiedDate(expectedLastModifiedDates)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withFundingDecision(expectedFundingDecisions)
                .withRejection(expectedRejections)
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
        assertEquals(expectedFundingDecisions[0], first.getFundingDecision());
        assertEquals(expectedRejections[0], first.getRejection());
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
        assertEquals(expectedFundingDecisions[1], second.getFundingDecision());
        assertEquals(expectedRejections[1], second.getRejection());
        assertEquals(expectedProcessRoles[1], second.getProcessRole());
        assertEquals(expectedApplications[1], second.getApplication());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedCompetitions[1], second.getCompetition());
    }
}
