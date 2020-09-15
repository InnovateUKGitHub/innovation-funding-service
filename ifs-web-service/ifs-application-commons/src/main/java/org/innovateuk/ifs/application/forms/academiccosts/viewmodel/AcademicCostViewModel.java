package org.innovateuk.ifs.application.forms.academiccosts.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

public class AcademicCostViewModel implements BaseAnalyticsViewModel {

    private final String financesUrl;
    private final String applicationName;
    private final String competitionName;
    private final String organisationName;
    private final long applicationId;
    private final long sectionId;
    private final long organisationId;
    private final long applicationFinanceId;
    private final boolean applicant;
    private final boolean includeVat;
    private final boolean open;
    private final boolean complete;

    public AcademicCostViewModel(String financesUrl, String applicationName, String competitionName, String organisationName, long applicationId, long sectionId, long organisationId, long applicationFinanceId, boolean applicant, boolean includeVat, boolean open, boolean complete) {
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.organisationName = organisationName;
        this.competitionName = competitionName;
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.organisationId = organisationId;
        this.applicationFinanceId = applicationFinanceId;
        this.applicant = applicant;
        this.includeVat = includeVat;
        this.open = open;
        this.complete = complete;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public long getOrganisationId() {
        return organisationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public long getSectionId() {
        return sectionId;
    }

    public boolean isIncludeVat() {
        return includeVat;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public long getApplicationFinanceId() {
        return applicationFinanceId;
    }

    public boolean isApplicant() {
        return applicant;
    }

    /* View logic. */
    public boolean isReadOnly() {
        return complete || !open;
    }
}
