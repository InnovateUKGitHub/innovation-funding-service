package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

public class ApplicationProcurementMilestonesViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean complete;
    private final boolean open;

    public ApplicationProcurementMilestonesViewModel(ApplicationResource application, ApplicationFinanceResource finance, String financesUrl, boolean complete, boolean open) {
        super(application.getDurationInMonths(), finance);
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.financesUrl = financesUrl;
        this.complete = complete;
        this.open = open;
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

    @Override
    public boolean isReadOnly() {
        return complete || !open;
    }
}
