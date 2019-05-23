package org.innovateuk.ifs.application.common.viewmodel;

public class ApplicationTermsPartnerRowViewModel {
    private final String name;
    private final boolean lead;
    private final boolean termsAccepted;

    public ApplicationTermsPartnerRowViewModel(String name, boolean lead, boolean termsAccepted) {
        this.name = name;
        this.lead = lead;
        this.termsAccepted = termsAccepted;
    }

    public String getName() {
        return name;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isTermsAccepted() {
        return termsAccepted;
    }
}