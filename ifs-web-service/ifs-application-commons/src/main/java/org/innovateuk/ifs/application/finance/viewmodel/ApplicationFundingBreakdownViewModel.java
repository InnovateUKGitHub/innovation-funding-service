package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * View model for finance/finance-summary :: finance-breakdown-table.
 */
public class ApplicationFundingBreakdownViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final List<BreakdownTableRow> rows;
    private final boolean collaborativeProject;
    private final boolean ktpCompetition;
    private final List<FinanceRowType> financeRowTypes;
    private final boolean anyApplicantHasVat;

    public ApplicationFundingBreakdownViewModel(long applicationId, String competitionName, List<BreakdownTableRow> rows, boolean collaborativeProject, boolean ktpCompetition, List<FinanceRowType> financeRowTypes, boolean anyApplicantHasVat) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.rows = rows;
        this.collaborativeProject = collaborativeProject;
        this.ktpCompetition = ktpCompetition;
        this.financeRowTypes = financeRowTypes;
        this.anyApplicantHasVat = anyApplicantHasVat;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public List<BreakdownTableRow> getRows() {
        return rows;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    public List<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }

    /* view logic. */
    public BigDecimal getTotal() {
        return rows.stream()
                .map(BreakdownTableRow::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTypeTotal(FinanceRowType type) {
        return rows.stream()
                .map(row -> row.getCost(type))
                .map(cost -> ofNullable(cost).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}