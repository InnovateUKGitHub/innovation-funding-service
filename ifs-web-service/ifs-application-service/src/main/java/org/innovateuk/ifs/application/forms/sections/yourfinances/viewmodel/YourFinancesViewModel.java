package org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class YourFinancesViewModel {
    private final long applicationId;
    private final String applicationName;
    private final boolean h2020;
    private final boolean collaborativeProject;
    private final boolean fullyFunded;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final List<YourFinancesRowViewModel> rows;

    public YourFinancesViewModel(long applicationId, String applicationName, CompetitionResource competition, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, List<YourFinancesRowViewModel> rows) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.h2020 = competition.isH2020();
        this.collaborativeProject = !CollaborationLevel.SINGLE.equals(competition.getCollaborationLevel());
        this.fullyFunded = competition.isFullyFunded();
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.rows = rows;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isH2020() {
        return h2020;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isFullyFunded() {
        return fullyFunded;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public List<YourFinancesRowViewModel> getRows() {
        return rows;
    }
}
