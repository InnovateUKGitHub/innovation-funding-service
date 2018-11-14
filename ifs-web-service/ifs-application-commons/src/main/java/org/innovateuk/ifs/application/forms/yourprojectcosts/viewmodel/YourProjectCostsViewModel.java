package org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

public class YourProjectCostsViewModel {
    private final Long applicationId;

    private final Long sectionId;

    private final Long competitionId;

    private final boolean complete;

    private final boolean open;

    private final String applicationName;

    private final String organisationName;

    private final String financesUrl;

    private final boolean internal;

    public YourProjectCostsViewModel(long applicationId, long sectionId, long competitionId, boolean complete, boolean open, String applicationName, String organisationName, String financesUrl) {
        this.internal = false;

        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.competitionId = competitionId;
        this.complete = complete;
        this.open = open;
        this.applicationName = applicationName;
        this.organisationName = organisationName;
        this.financesUrl = financesUrl;
    }

    public YourProjectCostsViewModel(boolean open) {
        this.open = open;
        this.complete = false;
        this.internal = true;

        this.applicationName = null;
        this.organisationName = null;
        this.financesUrl = null;
        this.applicationId = null;
        this.sectionId = null;
        this.competitionId = null;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getSectionId() {
        return sectionId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isInternal() {
        return internal;
    }

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }

    public boolean isReadOnly(FinanceRowType type) {
        return isReadOnly();
    }
}
