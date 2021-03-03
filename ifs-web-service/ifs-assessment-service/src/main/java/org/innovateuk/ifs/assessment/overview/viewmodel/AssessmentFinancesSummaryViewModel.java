package org.innovateuk.ifs.assessment.overview.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;

/**
 * Holder of model attributes for the Assessment Finances Summary view.
 */
public class AssessmentFinancesSummaryViewModel {

    private final long assessmentId;
    private final long applicationId;
    private final String applicationName;
    private final long daysLeft;
    private final long daysLeftPercentage;
    private final FundingType fundingType;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public AssessmentFinancesSummaryViewModel(long assessmentId, long applicationId, String applicationName, long daysLeft, long daysLeftPercentage, FundingType fundingType, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel, ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.fundingType = fundingType;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
    }

    public long getAssessmentId() {
        return assessmentId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getDaysLeft() {
        return daysLeft;
    }

    public long getDaysLeftPercentage() {
        return daysLeftPercentage;
    }

    public FundingType getFundingType() {
        return fundingType;
    }

    public boolean isKtpCompetition() {
        return FundingType.KTP.equals(fundingType);
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessmentFinancesSummaryViewModel that = (AssessmentFinancesSummaryViewModel) o;

        return new EqualsBuilder()
                .append(assessmentId, that.assessmentId)
                .append(applicationId, that.applicationId)
                .append(daysLeft, that.daysLeft)
                .append(daysLeftPercentage, that.daysLeftPercentage)
                .append(applicationName, that.applicationName)
                .append(fundingType, that.fundingType)
                .append(applicationFinanceSummaryViewModel, that.applicationFinanceSummaryViewModel)
                .append(applicationFundingBreakdownViewModel, that.applicationFundingBreakdownViewModel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(assessmentId)
                .append(applicationId)
                .append(applicationName)
                .append(daysLeft)
                .append(daysLeftPercentage)
                .append(fundingType)
                .append(applicationFinanceSummaryViewModel)
                .append(applicationFundingBreakdownViewModel)
                .toHashCode();
    }
}