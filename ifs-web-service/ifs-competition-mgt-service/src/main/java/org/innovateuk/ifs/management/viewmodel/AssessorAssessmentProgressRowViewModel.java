package org.innovateuk.ifs.management.viewmodel;

/**
 * Holder of model attributes for the applications shown in the 'Assessor progress' page
 */
public abstract class AssessorAssessmentProgressRowViewModel {
    private long applicationId;
    private String applicationName;
    private String leadOrganisation;

    protected AssessorAssessmentProgressRowViewModel(long applicationId, String applicationName, String leadOrganisation) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }
}
