package org.innovateuk.ifs.application.resource;

import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.junit.Test;

import java.util.EnumSet;
import java.util.function.Function;

import static java.util.EnumSet.of;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ApplicationAssessorResourceTest {

    @Test
    public void isAssigned() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(CREATED, PENDING, ACCEPTED, OPEN,
                DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT, SUBMITTED), ApplicationAssessorResource::isAssigned);
    }

    @Test
    public void isAccepted() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(ACCEPTED, OPEN, DECIDE_IF_READY_TO_SUBMIT,
                READY_TO_SUBMIT, SUBMITTED), ApplicationAssessorResource::isAccepted);
    }

    @Test
    public void isNotified() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(PENDING, REJECTED, ACCEPTED, OPEN,
                DECIDE_IF_READY_TO_SUBMIT, READY_TO_SUBMIT, SUBMITTED, WITHDRAWN), ApplicationAssessorResource::isNotified);
    }

    @Test
    public void isStarted() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(OPEN, DECIDE_IF_READY_TO_SUBMIT,
                READY_TO_SUBMIT, SUBMITTED), ApplicationAssessorResource::isStarted);
    }

    @Test
    public void isSubmitted() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(SUBMITTED), ApplicationAssessorResource::isSubmitted);
    }

    @Test
    public void isRejected() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(AssessmentState.REJECTED), ApplicationAssessorResource::isRejected);
    }

    @Test
    public void isWithdrawn() throws Exception {
        assertTrueOnlyForMostRecentAssessmentStates(of(AssessmentState.WITHDRAWN), ApplicationAssessorResource::isWithdrawn);
    }

    private void assertTrueOnlyForMostRecentAssessmentStates(EnumSet<AssessmentState>
                                                                     statesExpectedTrue, Function<ApplicationAssessorResource, Boolean> testable) {
        ApplicationAssessorResource applicationAssessorResource = new ApplicationAssessorResource();
        statesExpectedTrue.forEach(state -> {
            applicationAssessorResource.setMostRecentAssessmentState(state);
            assertTrue("Expected to be true for state: " + state, testable.apply(applicationAssessorResource));
        });

        EnumSet.complementOf(statesExpectedTrue).forEach(state -> {
            applicationAssessorResource.setMostRecentAssessmentState(state);
            assertFalse("Expected to be false for state: " + state, testable.apply(applicationAssessorResource));
        });
    }
}