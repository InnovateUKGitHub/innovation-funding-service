package org.innovateuk.ifs.project.grantofferletter.viewmodel;

public class ProcurementGrantOfferLetterTemplateViewModel {

    private final long applicationId;
    private final String organisationName;

    public ProcurementGrantOfferLetterTemplateViewModel(long applicationId, String organisationName) {
        this.applicationId = applicationId;
        this.organisationName = organisationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }
}