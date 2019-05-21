package org.innovateuk.ifs.application.common.viewmodel;

public class ApplicationTermsPartnerRowViewModel {
    private final String organisationName;
    private final boolean lead;
    private final boolean termsAccepted;

    public ApplicationTermsPartnerRowViewModel(String organisationName, boolean lead, boolean termsAccepted) {
        this.organisationName = organisationName;
        this.lead = lead;
        this.termsAccepted = termsAccepted;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }
}