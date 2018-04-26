package org.innovateuk.ifs.finance.resource.totals;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Resource is used for sending finance cost totals to the finance-data-service.
 */
public class FinanceCostTotalResource {

    @NotNull
    private FinanceType financeType;
    @NotNull
    private FinanceRowType financeRowType;
    @NotNull
    private BigDecimal total;
    @NotNull
    private Long financeId;

    public FinanceCostTotalResource(
            FinanceType financeType,
            FinanceRowType financeRowType,
            BigDecimal total,
            Long financeId
    ) {
        this.financeType = financeType;
        this.financeRowType = financeRowType;
        this.total = total;
        this.financeId = financeId;
    }

    public FinanceCostTotalResource() {
    }

    public FinanceType getFinanceType() {
        return financeType;
    }

    public void setFinanceType(FinanceType financeType) {
        this.financeType = financeType;
    }

    public FinanceRowType getFinanceRowType() {
        return financeRowType;
    }

    public void setFinanceRowType(FinanceRowType financeRowType) {
        this.financeRowType = financeRowType;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Long getFinanceId() {
        return financeId;
    }

    public void setFinanceId(Long financeId) {
        this.financeId = financeId;
    }
}