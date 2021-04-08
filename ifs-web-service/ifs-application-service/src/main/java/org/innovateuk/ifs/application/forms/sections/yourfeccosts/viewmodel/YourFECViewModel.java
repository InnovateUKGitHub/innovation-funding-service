package org.innovateuk.ifs.application.forms.sections.yourfeccosts.viewmodel;

import org.innovateuk.ifs.application.forms.sections.common.viewmodel.CommonYourProjectFinancesViewModel;

public class YourFECViewModel extends CommonYourProjectFinancesViewModel {
    private final long applicationFinanceId;

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
                            long applicationFinanceId) {
        super(financesUrl,competitionName,applicationName, applicationId, sectionId, open, h2020, complete, procurementCompetition,international);
        this.applicationFinanceId = applicationFinanceId;
    }

    public long getApplicationFinanceId() {
        return applicationFinanceId;
    }
}
