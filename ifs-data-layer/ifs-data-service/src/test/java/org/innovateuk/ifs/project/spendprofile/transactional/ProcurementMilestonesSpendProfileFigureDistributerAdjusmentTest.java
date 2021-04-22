package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.project.spendprofile.transactional.ProcurementMilestonesSpendProfileFigureDistributer.OtherAndVat;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.OTHER_COSTS;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.VAT;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileCostCategorySummariesBuilder.newSpendProfileCostCategorySummaries;
import static org.innovateuk.ifs.project.spendprofile.builder.SpendProfileCostCategorySummaryBuilder.newSpendProfileCostCategorySummary;
import static org.junit.Assert.*;
import static org.springframework.util.ReflectionUtils.*;

public class ProcurementMilestonesSpendProfileFigureDistributerAdjusmentTest {

    @Test
    public void testAdjustCostsRemoveTooMuchVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
            // Total vat = 3 try to remove 4
        assertThrows("We should get an illegal state exception if we try to remove too much vat",
                IllegalStateException .class,
                () -> callAdjustCosts(toAdjust, valueOf(-4)));
    }

    @Test
    public void testAdjustCostsRemoveTooMuchOtherCosts() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );

        // Total other costs = 6, try to remove 7.
         assertThrows("We should get an illegal state exception if we try to remove too much other costs",
                 IllegalStateException.class,
                 ()-> callAdjustCosts(toAdjust, valueOf(7)));
    }

    @Test
    public void testAdjustCostsAddToVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0))
        );
        // Call method under test add 2 to vat (subtract 2 from other costs)
        List<OtherAndVat> adjusted = callAdjustCosts(toAdjust, valueOf(2));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsSubtractFromVat() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(2)).withVat(valueOf(1))
        );
        // Call method under test add 2 to other costs (subtract 2 from vat)
        List<OtherAndVat> adjusted = callAdjustCosts(toAdjust, valueOf(-2));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsAddToVatMultipleIterationsRequired() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(5)),
                new OtherAndVat().withOtherCost(valueOf(1)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(5)).withVat(valueOf(1))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(7)).withVat(valueOf(8)),
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(3))
        );
        // Call method under test add 6 to vat (subtract 6 from other costs).
        // This means we will need to add more than one to some of the vat figures (and subtract more than one from some
        // of the other costs figures)
        List<OtherAndVat> adjusted = callAdjustCosts(toAdjust, valueOf(6));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testAdjustCostsAddToOtherCostsMultipleIterationsRequired() {
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(5)),
                new OtherAndVat().withOtherCost(valueOf(3)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(20)).withVat(valueOf(10))
        );
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(12)).withVat(valueOf(3)),
                new OtherAndVat().withOtherCost(valueOf(4)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(21)).withVat(valueOf(9))
        );
        // Call method under test add 4 to other costs (subtract 4 from vat)
        List<OtherAndVat> adjusted = callAdjustCosts(toAdjust, valueOf(-4));
        assertEquals(expected, adjusted);
    }

    @Test
    public void testToAdjustAmountToChangeOtherAndVatNotTheSame() {
        List<OtherAndVat> toAdjust = asList(new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(4)));

        SpendProfileCostCategorySummaries costCategorySummaries =
                newSpendProfileCostCategorySummaries()
                .withCosts(asList(
                        newSpendProfileCostCategorySummary()
                                .withTotal(new BigDecimal("11"))
                                .withCategory(
                                        newCostCategory()
                                                .withName(VAT.getDisplayName())
                                                .build())
                                .build(),
                        newSpendProfileCostCategorySummary()
                                .withTotal(new BigDecimal("2"))
                                .withCategory(
                                        newCostCategory()
                                                .withName(OTHER_COSTS.getDisplayName())
                                                .build())
                                .build()
                        ))
                        .build();
        // Other costs 10, vat 4. Adjusted to other 11, vat 2. Thus we need to add 1 to other and subtract 2 from vat.
        // This should give an error as we are not changing by the same absolute amount
        assertThrows("We should get an exception if we try to adjust by different absolute amounts",
                IllegalStateException .class,
                () -> callAdjustCosts(toAdjust, costCategorySummaries));

    }

    @Test
    public void testToAdjustAmountToChange() {
        // Vat rate 0.2. Costs of 42. Vat of 8.4 => 8. Total = 42 + 8 = 50
        SpendProfileCostCategorySummaries costCategorySummaries =
                newSpendProfileCostCategorySummaries()
                        .withCosts(asList(
                                newSpendProfileCostCategorySummary()
                                        .withTotal(new BigDecimal("42"))
                                        .withCategory(
                                                newCostCategory()
                                                        .withName(OTHER_COSTS.getDisplayName())
                                                        .build())
                                        .build(),
                                newSpendProfileCostCategorySummary()
                                        .withTotal(new BigDecimal("8"))
                                        .withCategory(
                                                newCostCategory()
                                                        .withName(VAT.getDisplayName())
                                                        .build())
                                        .build()
                        ))
                        .build();

        // 5 milestone of 10. Total = 50. Vat rate = 20. Per milestone Other = 8.33... => 8, Vat = 1.66... => 2
        List<OtherAndVat> toAdjust = asList(
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2))
                );
        // Need to add 2 to other and remove 2 from vat to arrive at totals of Other = 42 and Vat = 8
        List<OtherAndVat> expected = asList(
                new OtherAndVat().withOtherCost(valueOf(9)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(9)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2))
        );
        // Call the method under test
        assertEquals(expected, callAdjustCosts(toAdjust, costCategorySummaries));
    }


    private List<OtherAndVat> callAdjustCosts(List<OtherAndVat> toAdjust, SpendProfileCostCategorySummaries costCategorySummaries){
        Method adjustedCostsMethod = findMethod(ProcurementMilestonesSpendProfileFigureDistributer.class, "adjustedCosts", List.class, SpendProfileCostCategorySummaries.class);
        makeAccessible(adjustedCostsMethod);
        return (List<OtherAndVat>) invokeMethod(adjustedCostsMethod, new ProcurementMilestonesSpendProfileFigureDistributer(), toAdjust, costCategorySummaries);
    }

    private List<OtherAndVat> callAdjustCosts(List<OtherAndVat> toAdjust, BigInteger amountToAddToVat){
        Method adjustedCostsMethod = findMethod(ProcurementMilestonesSpendProfileFigureDistributer.class, "adjustedCosts", List.class, BigInteger.class);
        makeAccessible(adjustedCostsMethod);
        return (List<OtherAndVat>) invokeMethod(adjustedCostsMethod, new ProcurementMilestonesSpendProfileFigureDistributer(), toAdjust, amountToAddToVat);
    }
}
