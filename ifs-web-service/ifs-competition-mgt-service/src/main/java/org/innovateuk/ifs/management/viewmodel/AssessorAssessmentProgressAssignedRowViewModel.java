package org.innovateuk.ifs.management.viewmodel;

public class AssessorAssessmentProgressAssignedRowViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String leadOrganisation;
    private final int totalAssessors;

    public AssessorAssessmentProgressAssignedRowViewModel(long applicationId,
                                                          String applicationName,
                                                          String leadOrganisation,
                                                          int totalAssessors) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
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
}
