package org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;

public class ApplicationProcurementMilestonesViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean complete;
    private final boolean open;
    private final String applicationDetailsUrl;
    private final boolean projectCostsComplete;

    public ApplicationProcurementMilestonesViewModel(ApplicationResource application, ApplicationFinanceResource finance,
                                                     String financesUrl, boolean complete, boolean open,
                                                     String applicationDetailsUrl, boolean projectCostsComplete) {
        super(application.getDurationInMonths(), finance);
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.financesUrl = financesUrl;
        this.complete = complete;
        this.open = open;
        this.applicationDetailsUrl = applicationDetailsUrl;
        this.projectCostsComplete = projectCostsComplete;
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

    public String getApplicationDetailsUrl() {
        return applicationDetailsUrl;
    }

    public boolean isProjectCostsComplete() {
        return projectCostsComplete;
    }

    /* view logic */
    public boolean isHasDurations() {
        return !getDurations().isEmpty();
    }

    public boolean isDisplayProjectCostsBanner() {
        return !isReadOnly() && !projectCostsComplete;
    }

    @Override
    public boolean isReadOnly() {
        return complete || !open;
    }
}
