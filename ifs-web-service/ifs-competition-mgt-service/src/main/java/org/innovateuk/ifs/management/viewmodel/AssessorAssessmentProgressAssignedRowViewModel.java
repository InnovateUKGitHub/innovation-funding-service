package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentStates;

public class AssessorAssessmentProgressAssignedRowViewModel {

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
}
