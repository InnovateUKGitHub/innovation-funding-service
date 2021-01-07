package org.innovateuk.ifs.project.milestones.viewmodel;

import org.innovateuk.ifs.application.forms.sections.procurement.milestones.viewmodel.AbstractProcurementMilestoneViewModel;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectProcurementMilestoneViewModel extends AbstractProcurementMilestoneViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String financesUrl;
    private final boolean open;

    public ProjectProcurementMilestoneViewModel(ProjectResource project, ProjectFinanceResource finance, String financesUrl, boolean open) {
        super(project.getDurationInMonths(), finance);
        this.applicationId = project.getApplication();
        this.applicationName = project.getName();
        this.financesUrl = financesUrl;
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


    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean isReadOnly() {
        return !open;
    }
}