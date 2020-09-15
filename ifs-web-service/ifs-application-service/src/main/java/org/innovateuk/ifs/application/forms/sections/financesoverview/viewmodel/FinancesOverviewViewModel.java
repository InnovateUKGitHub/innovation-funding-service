package org.innovateuk.ifs.application.forms.sections.financesoverview.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.LOAN;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.PROCUREMENT;

public class FinancesOverviewViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String applicationName;
    private final String competitionName;
    private final Double researchParticipationPercentage;
    private final Integer maxResearchRatio;
    private final FundingType fundingType;
    private final String hint;
    private final String fundingRules;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public FinancesOverviewViewModel(long applicationId, String competitionName, String applicationName, Double researchParticipationPercentage, Integer maxResearchRatio, FundingType fundingType, String hint, String fundingRules, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.competitionName = competitionName;
        this.researchParticipationPercentage = researchParticipationPercentage;
        this.maxResearchRatio = maxResearchRatio;
        this.fundingType = fundingType;
        this.hint = hint;
        this.fundingRules = fundingRules;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
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

    public boolean isLoan() {
        return LOAN.equals(fundingType);
    }

    public boolean hasFundingRules() {
        return fundingRules != null;
    }

}
