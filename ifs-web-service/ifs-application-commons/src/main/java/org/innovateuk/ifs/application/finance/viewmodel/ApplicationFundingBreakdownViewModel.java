package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

/**
 * View model for finance/finance-summary :: finance-breakdown-table.
 */
public class ApplicationFundingBreakdownViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final List<BreakdownTableRow> rows;
    private final boolean collaborativeProject;
    private final Set<FinanceRowType> financeRowTypes;
    private final boolean anyApplicantHasVat;

    public ApplicationFundingBreakdownViewModel(long applicationId, String competitionName, List<BreakdownTableRow> rows, boolean collaborativeProject, Set<FinanceRowType> financeRowTypes, boolean anyApplicantHasVat) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.rows = rows;
        this.collaborativeProject = collaborativeProject;
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

    public Set<FinanceRowType> getFinanceRowTypes() {
        return financeRowTypes;
    }

    /* view logic. */
    public boolean isHasLabour() {
        return financeRowTypes.contains(LABOUR);
    }

    public boolean isHasOverheads() {
        return financeRowTypes.contains(OVERHEADS);
    }

    public boolean isHasProcurementOverheads() {
        return financeRowTypes.contains(PROCUREMENT_OVERHEADS);
    }

    public boolean isHasMaterials() {
        return financeRowTypes.contains(MATERIALS);
    }

    public boolean isHasCapitalUsage() {
        return financeRowTypes.contains(CAPITAL_USAGE);
    }

    public boolean isHasSubcontracting() {
        return financeRowTypes.contains(SUBCONTRACTING_COSTS);
    }
    public boolean isHasTravel() {
        return financeRowTypes.contains(TRAVEL);
    }

    public boolean isHasOther() {
        return financeRowTypes.contains(OTHER_COSTS);
    }

    public boolean isHasVat() {
        return anyApplicantHasVat;
    }

    public BigDecimal getTotal() {
        return rows.stream()
                .map(BreakdownTableRow::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getLabour() {
        return rows.stream()
                .map(BreakdownTableRow::getLabour)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOverheads() {
        return rows.stream()
                .map(BreakdownTableRow::getOverheads)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getProcurementOverheads() {
        return rows.stream()
                .map(BreakdownTableRow::getProcurementOverheads)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getMaterials() {
        return rows.stream()
                .map(BreakdownTableRow::getMaterials)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getCapitalUsage() {
        return rows.stream()
                .map(BreakdownTableRow::getCapitalUsage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getSubcontracting() {
        return rows.stream()
                .map(BreakdownTableRow::getSubcontracting)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTravel() {
        return rows.stream()
                .map(BreakdownTableRow::getTravel)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOther() {
        return rows.stream()
                .map(BreakdownTableRow::getOther)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getVat() {
        return rows.stream()
                .map(BreakdownTableRow::getVat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}