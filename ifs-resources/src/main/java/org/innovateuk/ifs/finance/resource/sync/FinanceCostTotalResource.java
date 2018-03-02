package org.innovateuk.ifs.finance.resource.sync;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import java.math.BigDecimal;

/**
 * Resource is used for sending finance cost totals to the finance-data-service.
 */
public class FinanceCostTotalResource {

    private FinanceType financeType;
    private FinanceRowType financeRowType;
    private BigDecimal total;
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