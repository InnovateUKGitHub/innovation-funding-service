package org.innovateuk.ifs.financecheck.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

/**
 * View model for the finance checks overview page.
 */
public class FinanceCheckOverviewViewModel {
    private final ProjectFinanceOverviewViewModel overview;
    private final FinanceCheckSummariesViewModel summaries;
    private final ProjectFinanceCostBreakdownViewModel breakdown;
    private final boolean loanCompetition;
    private final boolean ktpCompetition;
    private final boolean hasGrantClaimPercentage;
    private final boolean isThirdPartyOfgem;

    private final long applicationId;
    private final boolean canChangeFundingSought;
    private final boolean canChangeFundingLevelPercentages;

    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    private final boolean externalUser;

    private final String externalUserLinkUrl;

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel overview,
                                         FinanceCheckSummariesViewModel summaries,
                                         ProjectFinanceCostBreakdownViewModel breakdown,
                                         long applicationId,
                                         boolean canChangeFundingSought,
                                         boolean loanCompetition,
                                         boolean ktpCompetition,
                                         boolean canChangeFundingLevelPercentages,
                                         boolean hasGrantClaimPercentage,
                                         ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel,
                                         boolean isThirdPartyOfgem) {
        this(overview, summaries, breakdown, applicationId, canChangeFundingSought, loanCompetition, ktpCompetition, canChangeFundingLevelPercentages,
                hasGrantClaimPercentage, applicationFundingBreakdownViewModel, false, null, isThirdPartyOfgem);
    }

    public FinanceCheckOverviewViewModel(ProjectFinanceOverviewViewModel overview,
                                         FinanceCheckSummariesViewModel summaries,
                                         ProjectFinanceCostBreakdownViewModel breakdown,
                                         long applicationId,
                                         boolean canChangeFundingSought,
                                         boolean loanCompetition,
                                         boolean ktpCompetition,
                                         boolean canChangeFundingLevelPercentages,
                                         boolean hasGrantClaimPercentage,
                                         ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel,
                                         boolean externalUser,
                                         String externalUserLinkUrl,
                                         boolean isThirdPartyOfgem) {
        this.overview = overview;
        this.summaries = summaries;
        this.breakdown = breakdown;
        this.applicationId = applicationId;
        this.canChangeFundingSought = canChangeFundingSought;
        this.loanCompetition = loanCompetition;
        this.ktpCompetition = ktpCompetition;
        this.canChangeFundingLevelPercentages = canChangeFundingLevelPercentages;
        this.hasGrantClaimPercentage = hasGrantClaimPercentage;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.externalUser = externalUser;
        this.externalUserLinkUrl = externalUserLinkUrl;
        this.isThirdPartyOfgem = isThirdPartyOfgem;
    }

    public ProjectFinanceOverviewViewModel getOverview() {
        return overview;
    }

    public FinanceCheckSummariesViewModel getSummaries() {
        return summaries;
    }

    public ProjectFinanceCostBreakdownViewModel getBreakdown() {
        return breakdown;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public boolean isHasGrantClaimPercentage() {
        return hasGrantClaimPercentage;
    }

    public boolean isCanChangeFundingSought() {
        return canChangeFundingSought;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public boolean isCanChangeFundingLevelPercentages() {
        return canChangeFundingLevelPercentages;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    public boolean isExternalUser() {
        return externalUser;
    }

    public String getExternalUserLinkUrl() {
        return externalUserLinkUrl;
    }

    @JsonIgnore
    public boolean isProcurement() {
        return summaries.getFundingType() == FundingType.PROCUREMENT;
    }

    public boolean isThirdPartyOfgem() {
        return isThirdPartyOfgem;
    }
}