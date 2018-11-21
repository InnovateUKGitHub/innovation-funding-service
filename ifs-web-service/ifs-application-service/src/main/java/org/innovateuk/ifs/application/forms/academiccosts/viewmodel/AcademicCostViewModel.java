package org.innovateuk.ifs.application.forms.academiccosts.viewmodel;

public class AcademicCostViewModel {

    private final String financesUrl;
    private final String applicationName;
    private final String organisationName;
    private final long applicationId;
    private final long sectionId;
    private final long applicationFinanceId;
    private final boolean includeVat;
    private final boolean open;
    private final boolean complete;

    public AcademicCostViewModel(String financesUrl, String applicationName, String organisationName, long applicationId, long sectionId, long applicationFinanceId, boolean includeVat, boolean open, boolean complete) {
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.organisationName = organisationName;
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.applicationFinanceId = applicationFinanceId;
        this.includeVat = includeVat;
        this.open = open;
        this.complete = complete;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public long getApplicationId() {
        return applicationId;
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

    /* View logic. */
    public boolean isReadOnly() {
        return complete || !open;
    }
}
