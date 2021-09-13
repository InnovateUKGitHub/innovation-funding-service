package org.innovateuk.ifs.project.financechecks.viewmodel;

public class ProjectOrganisationRowViewModel {

    private final Long organisationId;
    private final String organisationName;
    private final boolean lead;
    private final boolean paymentMilestonesLink;
    private final boolean showChangesLink;
    private final boolean isFinanceChecksApproved;

    public ProjectOrganisationRowViewModel(Long organisationId, String organisationName, boolean lead,
                                           boolean paymentMilestonesLink, boolean showChangesLink,
                                           boolean isFinanceChecksApproved) {
        this.organisationId = organisationId;
        this.organisationName = organisationName;
        this.lead = lead;
        this.paymentMilestonesLink = paymentMilestonesLink;
        this.showChangesLink = showChangesLink;
        this.isFinanceChecksApproved = isFinanceChecksApproved;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLead() {
        return lead;
    }

    public boolean isPaymentMilestonesLink() {
        return paymentMilestonesLink;
    }

    public boolean isShowChangesLink() {
        return showChangesLink;
    }

    public boolean isFinanceChecksApproved() {
        return isFinanceChecksApproved;
    }
}
