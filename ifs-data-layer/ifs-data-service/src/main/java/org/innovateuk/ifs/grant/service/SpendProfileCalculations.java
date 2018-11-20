package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;

import java.math.BigDecimal;

public class SpendProfileCalculations {
    private SpendProfile spendProfile;

    public SpendProfileCalculations(SpendProfile spendProfile) {
        this.spendProfile = spendProfile;
    }

    protected BigDecimal getTotal() {
        return spendProfile.getSpendProfileFigures().getCosts().stream()
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    protected BigDecimal getTotalOverhead() {
        return spendProfile.getSpendProfileFigures().getCosts().stream()
                .filter(cost -> FinanceRowType.OVERHEADS.getName().equals(cost.getCostCategory().getName()))
                .map(Cost::getValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getOverheadPercentage() {
        return getTotalOverhead().divide(getTotal(), 2, BigDecimal.ROUND_HALF_UP);
    }

}
