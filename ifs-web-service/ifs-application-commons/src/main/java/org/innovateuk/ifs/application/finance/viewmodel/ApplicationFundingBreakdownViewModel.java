package org.innovateuk.ifs.application.finance.viewmodel;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import static org.innovateuk.ifs.finance.resource.cost.FinanceRowType.*;

/**
 * View model for finance/finance-summary :: finance-breakdown-table.
 */
public class ApplicationFundingBreakdownViewModel {

    private final long applicationId;
    private final List<BreakdownTableRow> rows;
    private final boolean collaborativeProject;
    private final Set<FinanceRowType> financeRowTypes;

    public ApplicationFundingBreakdownViewModel(long applicationId, List<BreakdownTableRow> rows, boolean collaborativeProject, Set<FinanceRowType> financeRowTypes) {
        this.applicationId = applicationId;
        this.rows = rows;
        this.collaborativeProject = collaborativeProject;
        this.financeRowTypes = financeRowTypes;
    }

    public long getApplicationId() {
        return applicationId;
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

}