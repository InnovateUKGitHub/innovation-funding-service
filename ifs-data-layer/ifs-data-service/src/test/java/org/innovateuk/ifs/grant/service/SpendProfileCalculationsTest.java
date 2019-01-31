package org.innovateuk.ifs.grant.service;

import org.hamcrest.Matchers;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.spendprofile.domain.SpendProfile;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileBuilder.newSpendProfile;

public class SpendProfileCalculationsTest {
    private static final CostCategory OVERHEADS = newCostCategory().withName("Overheads").build();
    private static final CostCategory LABOUR = newCostCategory().withName("Labour").build();

    private static final BigDecimal FIFTY_PERCENT = BigDecimal.valueOf(50);
    private static final BigDecimal HUNDRED_PERCENT =  BigDecimal.valueOf(100);

    @Test
    public void testOverheadCalculationFifty() {
        List<Cost> costs = asList(
                newOverheadCost(BigDecimal.ONE),
                newLabourCost(BigDecimal.ONE)
        );
        assertThat(FIFTY_PERCENT, Matchers.comparesEqualTo(newCalculations(costs).getOverheadPercentage()));
    }

    @Test
    public void testOverheadCalculationHundred() {
        List<Cost> costs = asList(
                newOverheadCost(BigDecimal.ONE),
                newLabourCost(BigDecimal.ZERO)
        );
        assertThat(HUNDRED_PERCENT, Matchers.comparesEqualTo(newCalculations(costs).getOverheadPercentage()));
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
