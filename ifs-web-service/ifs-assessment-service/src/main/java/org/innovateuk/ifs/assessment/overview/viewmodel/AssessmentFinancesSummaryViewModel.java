package org.innovateuk.ifs.assessment.overview.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFinanceSummaryViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationFundingBreakdownViewModel;
import org.innovateuk.ifs.application.finance.viewmodel.ApplicationProcurementMilestonesSummaryViewModel;
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
    private final ApplicationProcurementMilestonesSummaryViewModel applicationProcurementMilestonesSummaryViewModel;
    private final boolean procurementMilestones;
    private final boolean hecpCompetition;

    public AssessmentFinancesSummaryViewModel(long assessmentId, long applicationId, String applicationName, long daysLeft,
                                              long daysLeftPercentage, FundingType fundingType, ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel,
                                              ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel,
                                              ApplicationProcurementMilestonesSummaryViewModel applicationProcurementMilestonesSummaryViewModel,
                                              boolean procurementMilestones,
                                              boolean hecpCompetition) {
        this.assessmentId = assessmentId;
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.daysLeft = daysLeft;
        this.daysLeftPercentage = daysLeftPercentage;
        this.fundingType = fundingType;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.applicationProcurementMilestonesSummaryViewModel = applicationProcurementMilestonesSummaryViewModel;
        this.procurementMilestones = procurementMilestones;
        this.hecpCompetition = hecpCompetition;
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

    public boolean isHecpCompetition() {
        return hecpCompetition;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    public ApplicationProcurementMilestonesSummaryViewModel getApplicationProcurementMilestonesSummaryViewModel() {
        return applicationProcurementMilestonesSummaryViewModel;
    }

    public boolean isProcurementMilestones() {
        return procurementMilestones;
    }

    @JsonIgnore
    public boolean isProcurement() {
        return fundingType == FundingType.PROCUREMENT;
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
                .append(applicationProcurementMilestonesSummaryViewModel, that.applicationProcurementMilestonesSummaryViewModel)
                .append(procurementMilestones, that.procurementMilestones)
                .append(hecpCompetition, that.hecpCompetition)
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
                .append(applicationProcurementMilestonesSummaryViewModel)
                .append(procurementMilestones)
                .append(hecpCompetition)
                .toHashCode();
    }
}