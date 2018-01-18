package org.innovateuk.ifs.finance.resource.sync;

import java.math.BigDecimal;

/**
 * Resource is used for sending finance cost totals data to the finance-data-service.
 */
public class FinanceCostTotalResource {
    private String name;
    private BigDecimal total;
    private Long financeId;
    private String financeType;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getFinanceType() {
        return financeType;
    }

    public void setFinanceType(String financeType) {
        this.financeType = financeType;
    }
}
