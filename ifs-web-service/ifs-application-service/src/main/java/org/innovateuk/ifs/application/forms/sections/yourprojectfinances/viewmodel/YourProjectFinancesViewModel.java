package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.FinanceSummaryTableViewModel;
import org.innovateuk.ifs.competition.resource.CollaborationLevel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.util.List;

public class YourProjectFinancesViewModel implements BaseAnalyticsViewModel {
    private final long applicationId;
    private final String competitionName;
    private final String applicationName;
    private final boolean h2020;
    private final boolean collaborativeProject;
    private final boolean fullyFunded;
    private final boolean ktp;
    private final boolean isThirdPartyOfgem;
    private FinanceSummaryTableViewModel financeSummaryTableViewModel;
    private final List<YourFinancesRowViewModel> rows;
    private boolean eoiFullApplication;

    public YourProjectFinancesViewModel(long applicationId, String applicationName, CompetitionResource competition,
                                        FinanceSummaryTableViewModel financeSummaryTableViewModel, List<YourFinancesRowViewModel> rows,
                                        boolean isThirdPartyOfgem, boolean eoiFullApplication) {
        this.applicationId = applicationId;
        this.competitionName = competition.getName();
        this.applicationName = applicationName;
        this.h2020 = competition.isH2020();
        this.collaborativeProject = !CollaborationLevel.SINGLE.equals(competition.getCollaborationLevel());
        this.fullyFunded = competition.isFullyFunded();
        this.ktp = competition.isKtp();
        this.financeSummaryTableViewModel = financeSummaryTableViewModel;
        this.rows = rows;
        this.isThirdPartyOfgem = isThirdPartyOfgem;
        this.eoiFullApplication = eoiFullApplication;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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

    public FinanceSummaryTableViewModel getFinanceSummaryTableViewModel() {
        return financeSummaryTableViewModel;
    }

    public List<YourFinancesRowViewModel> getRows() {
        return rows;
    }

    public boolean isKtp() {
        return ktp;
    }

    public boolean isThirdPartyOfgem() {
        return isThirdPartyOfgem;
    }

    public boolean isEoiFullApplication() {
        return eoiFullApplication;
    }
}
