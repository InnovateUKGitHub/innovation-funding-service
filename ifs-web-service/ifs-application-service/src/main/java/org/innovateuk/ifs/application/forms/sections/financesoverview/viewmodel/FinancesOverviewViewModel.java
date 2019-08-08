package org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;

public class FinancesOverviewViewModel {

    private final long applicationId;
    private final String applicationName;
    private final Double researchParticipationPercentage;
    private final Integer maxResearchRatio;
    private final boolean procurement;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public FinancesOverviewViewModel(long applicationId, String applicationName, Double researchParticipationPercentage, Integer maxResearchRatio, boolean procurement, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.researchParticipationPercentage = researchParticipationPercentage;
        this.maxResearchRatio = maxResearchRatio;
        this.procurement = procurement;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Double getResearchParticipationPercentage() {
        return researchParticipationPercentage;
    }

    public Integer getMaxResearchRatio() {
        return maxResearchRatio;
    }

    public boolean isProcurement() {
        return procurement;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

}
