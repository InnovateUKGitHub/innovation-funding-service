package org.innovateuk.ifs.application.forms.yourprojectcosts.viewmodel;

public class YourProjectCostsViewModel {
    private final long applicationId;

    private final long sectionId;

    private final long competitionId;

    private final boolean complete;

    private final boolean open;

    private final boolean leadApplicant;

    private final String applicationName;

    private final String organisationName;

    private final String financesUrl;

    public YourProjectCostsViewModel(long applicationId, long sectionId, long competitionId, boolean complete, boolean open, boolean leadApplicant, String applicationName, String organisationName, String financesUrl) {
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.competitionId = competitionId;
        this.complete = complete;
        this.open = open;
        this.leadApplicant = leadApplicant;
        this.applicationName = applicationName;
        this.organisationName = organisationName;
        this.financesUrl = financesUrl;
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

    public boolean isLeadApplicant() {
        return leadApplicant;
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

    /* view logic */
    public boolean isReadOnly() {
        return complete || !open;
    }
}
