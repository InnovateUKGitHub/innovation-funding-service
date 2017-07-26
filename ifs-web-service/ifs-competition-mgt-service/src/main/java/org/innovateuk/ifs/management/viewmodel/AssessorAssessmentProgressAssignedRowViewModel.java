package org.innovateuk.ifs.management.viewmodel;

import com.google.common.collect.Sets;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;

import java.util.Set;

public class AssessorAssessmentProgressAssignedRowViewModel {

    private static final Set<AssessmentStates> NOTIFIED_STATES = Sets.immutableEnumSet(
            AssessmentStates.PENDING,
            AssessmentStates.ACCEPTED,
            AssessmentStates.OPEN,
            AssessmentStates.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentStates.READY_TO_SUBMIT,
            AssessmentStates.SUBMITTED
    );

    private static final Set<AssessmentStates> ACCEPTED_STATES = Sets.immutableEnumSet(
            AssessmentStates.ACCEPTED,
            AssessmentStates.OPEN,
            AssessmentStates.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentStates.READY_TO_SUBMIT,
            AssessmentStates.SUBMITTED
    );

    private static final Set<AssessmentStates> STARTED_STATES = Sets.immutableEnumSet(
            AssessmentStates.OPEN,
            AssessmentStates.DECIDE_IF_READY_TO_SUBMIT,
            AssessmentStates.READY_TO_SUBMIT,
            AssessmentStates.SUBMITTED
    );

    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisation;
    private final int totalAssessors;
    private AssessmentStates state;

    public AssessorAssessmentProgressAssignedRowViewModel(long applicationId,
                                                          String applicationName,
                                                          String leadOrganisation,
                                                          int totalAssessors,
                                                          AssessmentStates state) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
        this.state = state;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public int getTotalAssessors() {
        return totalAssessors;
    }

    public AssessmentStates getState() {
        return state;
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
        return state == AssessmentStates.SUBMITTED;
    }
}
