package org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel;

import java.util.List;

public class YourFinancesRowViewModel {

    private final long applicationId;
    private final String applicationName;
    private final List<YourFinancesRowViewModel> rows;

    public YourFinancesRowViewModel(long applicationId, String applicationName, List<YourFinancesRowViewModel> rows) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.rows = rows;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public List<YourFinancesRowViewModel> getRows() {
        return rows;
    }
}
