package org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel;

import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;

public class FinancesOverviewViewModel {

    private final long applicationId;
    private final String applicationName;
    private final Double researchParticipationPercentage;
    private final Integer maxResearchRatio;
    private final FundingType fundingType;
    private final String hint;
    private final String fundingRules;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public FinancesOverviewViewModel(long applicationId, String applicationName, Double researchParticipationPercentage, Integer maxResearchRatio, FundingType fundingType, String hint, String fundingRules, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.researchParticipationPercentage = researchParticipationPercentage;
        this.maxResearchRatio = maxResearchRatio;
        this.fundingType = fundingType;
        this.hint = hint;
        this.fundingRules = fundingRules;
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

    public String getHint() {
        return hint;
    }

    public String getFundingRules() {
        return fundingRules;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    public boolean isProcurement() {
        return PROCUREMENT.equals(fundingType);
    }

    public boolean hasFundingRules() {
        return fundingRules != null;
    }

}
