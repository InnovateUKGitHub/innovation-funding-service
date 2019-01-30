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
                .filter(cost -> FinanceRowType.OVERHEADS.getName().equals(cost.getCostCategory().getName()))
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal getOverheadPercentage() {
        return getTotalOverhead().multiply(BigDecimal.valueOf(100))
                .divide(getTotal(), 2, BigDecimal.ROUND_HALF_UP);
    }
}
