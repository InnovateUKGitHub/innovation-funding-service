package org.innovateuk.ifs.application.domain;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.workflow.domain.ActivityState;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.EnumSet;

import static org.innovateuk.ifs.application.builder.ApplicationStatisticsBuilder.newApplicationStatistics;
import static org.innovateuk.ifs.assessment.builder.AssessmentBuilder.newAssessment;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.innovateuk.ifs.workflow.domain.ActivityType.APPLICATION_ASSESSMENT;
import static org.junit.Assert.assertEquals;

public class ApplicationStatisticsTest {

    ApplicationStatistics applicationStatistics;

    @Before
    public void setup() throws Exception {
        applicationStatistics = newApplicationStatistics()
                .withAssessments(
                        newAssessment().withActivityState(
                                Arrays.stream(
                                        AssessmentState.values())
                                        .map(x -> new ActivityState(APPLICATION_ASSESSMENT, x.getBackingState()))
                                        .toArray(ActivityState[]::new))
                                .build(AssessmentState.values().length))
                .build();
    }

    @Test
    public void assessorCount() {
        int expectedCount = EnumSet.of(CREATED, PENDING, ACCEPTED, OPEN, DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT, SUBMITTED).size();
        assertEquals(expectedCount, applicationStatistics.getAssessors());
    }

    @Test
    public void acceptedCount() {
        int expectedCount = EnumSet.of(ACCEPTED, OPEN, DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT, SUBMITTED).size();
        assertEquals(expectedCount, applicationStatistics.getAccepted());
    }

    @Test
    public void submittedCount() {
        int expectedCount = EnumSet.of(SUBMITTED).size();
        assertEquals(expectedCount, applicationStatistics.getSubmitted());
    }
}
