package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.domain.Assessment;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.junit.Test;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.builder.AssessmentFundingDecisionOutcomeBuilder.newAssessmentFundingDecisionOutcome;
import static org.innovateuk.ifs.assessment.builder.AssessmentRejectOutcomeBuilder.newAssessmentRejectOutcome;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.READY_TO_SUBMIT;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.*;

public class AssessmentBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessState expectedStatus = AssessmentState.OPEN;
        ZonedDateTime expectedLastModifiedDate = ZonedDateTime.now();
        LocalDate expectedStartDate = LocalDate.now().minusDays(2);
        LocalDate expectedEndDate = LocalDate.now().minusDays(1);
        ProcessRole expectedProcessRole = newProcessRole().build();
        AssessmentFundingDecisionOutcome expectedFundingDecision = newAssessmentFundingDecisionOutcome().build();
        AssessmentRejectOutcome expectedRejection = newAssessmentRejectOutcome().build();

        Assessment assessment = newAssessment()
                .withId(expectedId)
                .withActivityState(new ActivityState(APPLICATION_ASSESSMENT, AssessmentState.OPEN.getBackingState()))
                .withLastModifiedDate(expectedLastModifiedDate)
                .withStartDate(expectedStartDate)
                .withEndDate(expectedEndDate)
                .withParticipant(expectedProcessRole)
                .withFundingDecision(expectedFundingDecision)
                .withRejection(expectedRejection)
                .build();

        assertEquals(expectedId, assessment.getId());
        assertEquals(expectedStatus, assessment.getActivityState());
        assertEquals(expectedLastModifiedDate, assessment.getLastModified());
        assertEquals(expectedStartDate, assessment.getStartDate());
        assertEquals(expectedEndDate, assessment.getEndDate());
        assertEquals(expectedProcessRole, assessment.getParticipant());
        assertEquals(expectedFundingDecision, assessment.getFundingDecision());
        assertEquals(expectedRejection, assessment.getRejection());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessState[] expectedStatuss = {AssessmentState.OPEN, READY_TO_SUBMIT};
        ZonedDateTime[] expectedLastModifiedDates = {ZonedDateTime.now(), ZonedDateTime.now()};
        LocalDate[] expectedStartDates = {LocalDate.now().minusDays(2), LocalDate.now().minusDays(3)};
        LocalDate[] expectedEndDates = {LocalDate.now().minusDays(1), LocalDate.now().minusDays(2)};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);
        AssessmentFundingDecisionOutcome[] expectedFundingDecision = newAssessmentFundingDecisionOutcome()
                .buildArray(2, AssessmentFundingDecisionOutcome.class);
        AssessmentRejectOutcome[] expectedRejection = newAssessmentRejectOutcome()
                .buildArray(2, AssessmentRejectOutcome.class);

        List<Assessment> assessments = newAssessment()
                .withId(expectedIds)
                .withActivityState(
                        new ActivityState(APPLICATION_ASSESSMENT, AssessmentState.OPEN.getBackingState()),
                        new ActivityState(APPLICATION_ASSESSMENT, READY_TO_SUBMIT.getBackingState()))
                .withLastModifiedDate(expectedLastModifiedDates)
                .withStartDate(expectedStartDates)
                .withEndDate(expectedEndDates)
                .withParticipant(expectedProcessRoles)
                .withFundingDecision(expectedFundingDecision)
                .withRejection(expectedRejection)
                .build(2);

        Assessment first = assessments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuss[0], first.getActivityState());
        assertEquals(expectedLastModifiedDates[0], first.getLastModified());
        assertEquals(expectedStartDates[0], first.getStartDate());
        assertEquals(expectedEndDates[0], first.getEndDate());
        assertEquals(expectedProcessRoles[0], first.getParticipant());
        assertEquals(expectedFundingDecision[0], first.getFundingDecision());
        assertEquals(expectedRejection[0], first.getRejection());

        Assessment second = assessments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuss[1], second.getActivityState());
        assertEquals(expectedLastModifiedDates[1], second.getLastModified());
        assertEquals(expectedStartDates[1], second.getStartDate());
        assertEquals(expectedEndDates[1], second.getEndDate());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
        assertEquals(expectedFundingDecision[1], second.getFundingDecision());
        assertEquals(expectedRejection[1], second.getRejection());
    }

}
