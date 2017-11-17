package org.innovateuk.ifs.management.viewmodel;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.assessment.resource.AssessmentState;

import java.util.Set;

public class AssessorAssessmentProgressAssignedRowViewModel extends AssessorAssessmentProgressRowViewModel {

    private static final Set<AssessmentState> NOTIFIED_STATES = Sets.immutableEnumSet(
            AssessmentState.PENDING,
            AssessmentState.ACCEPTED,
            AssessmentState.OPEN,
            AssessmentState.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentState.READY_TO_SUBMIT,
            AssessmentState.SUBMITTED
    );

    private static final Set<AssessmentState> ACCEPTED_STATES = Sets.immutableEnumSet(
            AssessmentState.ACCEPTED,
            AssessmentState.OPEN,
            AssessmentState.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentState.READY_TO_SUBMIT,
            AssessmentState.SUBMITTED
    );

    private static final Set<AssessmentState> STARTED_STATES = Sets.immutableEnumSet(
            AssessmentState.OPEN,
            AssessmentState.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentState.READY_TO_SUBMIT,
            AssessmentState.SUBMITTED
    );

    private int totalAssessors;
    private AssessmentState state;
    private long assessmentId;

    public AssessorAssessmentProgressAssignedRowViewModel(long applicationId,
                                                          String applicationName,
                                                          String leadOrganisation,
                                                          int totalAssessors,
                                                          AssessmentState state,
                                                          long assessmentId) {
        super(applicationId, applicationName, leadOrganisation);
        this.totalAssessors = totalAssessors;
        this.state = state;
        this.assessmentId = assessmentId;
    }

    public int getTotalAssessors() {
        return totalAssessors;
    }

    public AssessmentState getState() {
        return state;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public boolean isNotified() {
        return NOTIFIED_STATES.contains(state);
    }

    public boolean isAccepted() {
        return ACCEPTED_STATES.contains(state);
    }

    public boolean isStarted() {
        return STARTED_STATES.contains(state);
    }

    public boolean isSubmitted() {
        return state == AssessmentState.SUBMITTED;
    }
}
