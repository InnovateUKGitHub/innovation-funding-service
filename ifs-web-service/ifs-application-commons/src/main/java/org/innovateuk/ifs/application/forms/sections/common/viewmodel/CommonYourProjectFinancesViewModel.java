package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

/**
 * A view model that captures attributes common to all of the Your project finances sections.
 */
public class CommonYourProjectFinancesViewModel {

    private final String financesUrl;
    private final String applicationName;
    private final long applicationId;
    private final long sectionId;
    private final boolean open;
    private final boolean complete;
    private final boolean h2020;
    private final boolean procurementCompetition;


    public CommonYourProjectFinancesViewModel(String financesUrl,
                                              String applicationName,
                                              long applicationId,
                                              long sectionId,
                                              boolean open,
                                              boolean h2020,
                                              boolean complete,
                                              boolean procurementCompetition) {
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.sectionId = sectionId;
        this.open = open;
        this.h2020 = h2020;
        this.complete = complete;
        this.procurementCompetition = procurementCompetition;
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

    public boolean isH2020() {
            return h2020; }

    public boolean isComplete() {
        return complete;
    }

    public boolean isProcurementCompetition() {
        return procurementCompetition;
    }
}
