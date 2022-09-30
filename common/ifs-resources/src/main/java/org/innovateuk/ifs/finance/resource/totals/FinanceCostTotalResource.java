package org.innovateuk.ifs.finance.resource.totals;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Resource is used for sending finance cost totals to the finance-data-service.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinanceCostTotalResource {

    @NotNull
    private FinanceType financeType;
    @NotNull
    private FinanceRowType financeRowType;
    @NotNull
    private BigDecimal total;
    @NotNull
    private Long financeId;



}