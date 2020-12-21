package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class ApplicationProcurementMilestonesViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean complete;
    private final boolean open;

    public ApplicationProcurementMilestonesViewModel(ApplicationResource application, String financesUrl) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.financesUrl = financesUrl;
        this.complete = false;
        this.open = true;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getFinancesUrl() {
        return financesUrl;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isReadOnly() {
        return complete || !open;
    }
}
