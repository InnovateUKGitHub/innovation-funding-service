package org.innovateuk.ifs.eugrant.overview.viewmodel;

public class EuGrantOverviewViewModel {

    private final boolean organisationComplete;
    private final boolean contactComplete;
    private final boolean fundingComplete;

    public EuGrantOverviewViewModel(boolean organisationComplete, boolean contactComplete, boolean fundingComplete) {
        this.organisationComplete = organisationComplete;
        this.contactComplete = contactComplete;
        this.fundingComplete = fundingComplete;
    }

    public boolean isOrganisationComplete() {
        return organisationComplete;
    }

    public boolean isContactComplete() {
        return contactComplete;
    }

    public boolean isFundingComplete() {
        return fundingComplete;
    }

    /* View logic. */
    public boolean allComplete() { return organisationComplete && contactComplete && fundingComplete; }
}
