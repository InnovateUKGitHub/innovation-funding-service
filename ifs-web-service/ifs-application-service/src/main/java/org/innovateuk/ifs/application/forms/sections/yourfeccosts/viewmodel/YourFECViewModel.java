package org.innovateuk.ifs.application.forms.sections.yourfeccosts.viewmodel;

import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;

import java.time.ZonedDateTime;

public class YourFECViewModel extends CommonYourProjectFinancesViewModel {
    private final long applicationFinanceId;
    private final String lastUpdatedBy;
    private final ZonedDateTime lastUpdatedOn;

    public YourFECViewModel(String financesUrl,
                            String competitionName,
                            String applicationName,
                            long applicationId,
                            long sectionId,
                            boolean open,
                            boolean h2020,
                            boolean complete,
                            boolean procurementCompetition,
                            boolean international,
                            long applicationFinanceId,
                            String lastUpdatedBy,
                            ZonedDateTime lastUpdatedOn) {
        super(financesUrl,competitionName,applicationName, applicationId, sectionId, open, h2020, complete, procurementCompetition,international);
        this.applicationFinanceId = applicationFinanceId;
        this.lastUpdatedBy = lastUpdatedBy;
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public long getApplicationFinanceId() {
        return applicationFinanceId;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public ZonedDateTime getLastUpdatedOn() {
        return lastUpdatedOn;
    }
}
