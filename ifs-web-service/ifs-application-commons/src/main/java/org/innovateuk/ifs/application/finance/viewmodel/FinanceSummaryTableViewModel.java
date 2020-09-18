package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.negate;

/**
 * View model for finance/finance-summary :: application-finances-summary.
 */
public class FinanceSummaryTableViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final List<FinanceSummaryTableRow> rows;
    private final boolean readOnly;
    private final boolean collaborativeProject;
    private final boolean fundingLevelFirst;
    private final BigDecimal competitionMaximumFundingSought;
    private final boolean ktp;
    private final boolean includeOrganisationNames;

    public FinanceSummaryTableViewModel(long applicationId,
                                        CompetitionResource competition,
                                        List<FinanceSummaryTableRow> rows,
                                        boolean readOnly,
                                        boolean collaborativeProject,
                                        BigDecimal competitionMaximumFundingSought,
                                        boolean includeOrganisationNames) {
        this.applicationId = applicationId;
        this.competitionName = competition.getName();
        this.rows = rows;
        this.readOnly = readOnly;
        this.collaborativeProject = collaborativeProject;
        this.fundingLevelFirst = competition.getFinanceRowTypes().contains(FinanceRowType.FINANCE);
        this.competitionMaximumFundingSought = competitionMaximumFundingSought;
        this.ktp = competition.isKtp();
        this.includeOrganisationNames = includeOrganisationNames;
    }


    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public List<FinanceSummaryTableRow> getRows() {
        return rows;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isFundingLevelFirst() {
        return fundingLevelFirst;
    }

    public boolean isFundingSoughtFirst() {
        return !isFundingLevelFirst();
    }

    public BigDecimal getCompetitionMaximumFundingSought() {
        return competitionMaximumFundingSought;
    }

    public boolean isKtp() {
        return ktp;
    }

    public boolean isIncludeOrganisationNames() {
        return includeOrganisationNames;
    }

    public boolean isAllFinancesComplete() {
        return rows.stream()
                .filter(negate(FinanceSummaryTableRow::isPendingOrganisation))
                .allMatch(FinanceSummaryTableRow::isComplete) && isFundingSoughtValid();
    }

    public BigDecimal getCosts() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getCosts)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getClaimPercentage() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getClaimPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getFundingSought() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getFundingSought)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOtherFunding() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getOtherFunding)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getContribution() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getContribution)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getContributionPercentage() {
        return rows.stream()
                .map(FinanceSummaryTableRow::getContributionPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private boolean isFundingSoughtValid() {
        if (competitionMaximumFundingSought == null) {
            return true;
        }
        return rows.stream()
                .map(row -> row.getFundingSought())
                .reduce(BigDecimal::add).get().compareTo(competitionMaximumFundingSought) <= 0;
    }

    private boolean atLeastTwoCompleteOrganisationFinances() {
        return rows.stream().filter(FinanceSummaryTableRow::isComplete)
                .count() > 1;
    }

}
