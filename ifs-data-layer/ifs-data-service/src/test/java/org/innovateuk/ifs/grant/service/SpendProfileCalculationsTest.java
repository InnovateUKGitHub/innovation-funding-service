package org.innovateuk.ifs.grant.service;

import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;
import static org.junit.Assert.assertEquals;

public class SpendProfileCalculationsTest {
    private static final CostCategory OVERHEADS = newCostCategory().withName("Overheads").build();
    private static final CostCategory LABOUR = newCostCategory().withName("Labour").build();

    private static final BigDecimal FIFTY_PERCENT = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED_PERCENT =  BigDecimal.valueOf(100);

    @Test
    public void overheadCalculationZero() {
        List<Cost> costs = asList(
                newOverheadCost(BigDecimal.ZERO),
                newLabourCost(BigDecimal.ZERO)
        );
        assertEquals(BigDecimal.ZERO, newCalculations(costs).getOverheadPercentage());
    }

    @Test
    public void overheadCalculationFifty() {
        List<Cost> costs = asList(
                newOverheadCost(BigDecimal.ONE),
                newLabourCost(BigDecimal.ONE)
        );
        assertEquals(FIFTY_PERCENT, newCalculations(costs).getOverheadPercentage());
    }

    @Test
    public void overheadCalculationHundred() {
        List<Cost> costs = asList(
                newOverheadCost(BigDecimal.ONE),
                newLabourCost(BigDecimal.ZERO)
        );
        assertEquals(HUNDRED_PERCENT, newCalculations(costs).getOverheadPercentage());
    }

    private Cost newOverheadCost(BigDecimal value) {
        return newCost().withCostCategory(OVERHEADS).withValue(value).build();
    }

    private Cost newLabourCost(BigDecimal value) {
        return newCost().withCostCategory(LABOUR).withValue(value).build();
    }

    private SpendProfileCalculations newCalculations(List<Cost> costs) {
        return new SpendProfileCalculations(newSpendProfile().withSpendProfileFigures(costs).build());
    }
}
