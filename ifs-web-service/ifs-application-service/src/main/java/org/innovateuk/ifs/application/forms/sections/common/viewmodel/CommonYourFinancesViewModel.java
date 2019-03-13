package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

/**
 * A view model that captures attributes common to all of the Your finances sections.
 */
public class CommonYourFinancesViewModel {

    private final String financesUrl;
    private final String applicationName;
    private final long applicationId;
    private final long sectionId;
    private final boolean open;
    private final boolean complete;

    public CommonYourFinancesViewModel(String financesUrl, String applicationName, long applicationId, long sectionId, boolean open, boolean complete) {
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.open = open;
        this.complete = complete;
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

    public boolean isComplete() {
        return complete;
    }
}
