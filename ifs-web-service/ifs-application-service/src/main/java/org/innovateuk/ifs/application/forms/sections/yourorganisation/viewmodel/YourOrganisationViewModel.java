package org.innovateuk.ifs.application.forms.sections.yourorganisation.viewmodel;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

/**
 * View model to support project location page
 */
public class YourOrganisationViewModel {

    private final String financesUrl;
    private final String applicationName;
    private final long applicationId;
    private final long sectionId;
    private final boolean open;
    private final boolean complete;

    public YourOrganisationViewModel(
            boolean complete,
            String financesUrl,
            String applicationName,
            long applicationId,
            long sectionId,
            boolean open) {

        this.complete = complete;
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.open = open;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isReadOnly() {
        return complete || !open;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public long getSectionId() {
        return sectionId;
    }

    public boolean isOpen() {
        return open;
    }

    public OrganisationSize[] getOrganisationSizeOptions() {
        return OrganisationSize.values();
    }
}
