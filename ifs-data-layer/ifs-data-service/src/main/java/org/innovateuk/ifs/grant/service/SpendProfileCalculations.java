package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;

import java.math.BigDecimal;

class SpendProfileCalculations {
    private SpendProfile spendProfile;

    SpendProfileCalculations(SpendProfile spendProfile) {
        this.spendProfile = spendProfile;
    }

    private BigDecimal getTotal() {
        return spendProfile.getSpendProfileFigures().getCosts().stream()
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getTotalOverhead() {
        return spendProfile.getSpendProfileFigures().getCosts().stream()
                .filter(cost -> FinanceRowType.OVERHEADS.getDisplayName().equals(cost.getCostCategory().getName()))
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal getOverheadPercentage() {

        BigDecimal total = getTotal();

        if (total.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return getTotalOverhead().multiply(BigDecimal.valueOf(100))
                .divide(total, 2, BigDecimal.ROUND_HALF_UP);
    }
}
