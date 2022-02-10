package org.innovateuk.ifs.application.forms.sections.common.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

/**
 * A view model that captures attributes common to all of the Your project finances sections.
 */
public class CommonYourProjectFinancesViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final String financesUrl;
    private final String applicationName;
    private final long sectionId;
    private final boolean open;
    private final boolean complete;
    private final boolean h2020;
    private final boolean procurementCompetition;
    private final boolean international;


    public CommonYourProjectFinancesViewModel(String financesUrl,
                                              String competitionName,
                                              String applicationName,
                                              long applicationId,
                                              long sectionId,
                                              boolean open,
                                              boolean h2020,
                                              boolean complete,
                                              boolean procurementCompetition,
                                              boolean international) {
        this.financesUrl = financesUrl;
        this.applicationName = applicationName;
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.sectionId = sectionId;
        this.open = open;
        this.h2020 = h2020;
        this.complete = complete;
        this.procurementCompetition = procurementCompetition;
        this.international = international;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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

    public boolean isInternational() {
        return international;
    }
}
