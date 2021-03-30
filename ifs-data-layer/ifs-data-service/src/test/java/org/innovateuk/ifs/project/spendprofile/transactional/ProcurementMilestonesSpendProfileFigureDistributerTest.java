package org.innovateuk.ifs.project.spendprofile.transactional;

import org.innovateuk.ifs.procurement.milestone.resource.ProjectProcurementMilestoneResource;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.financechecks.domain.CostCategory;
import org.innovateuk.ifs.project.spendprofile.transactional.ProcurementMilestonesSpendProfileFigureDistributer.OtherAndVat;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import static java.math.BigInteger.valueOf;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.OTHER_COSTS;
import static org.innovateuk.ifs.finance.resource.cost.ProcurementCostCategoryGenerator.VAT;
import static org.innovateuk.ifs.procurement.milestone.builder.ProjectProcurementMilestoneResourceBuilder.newProjectProcurementMilestoneResource;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.junit.Assert.*;
import static org.springframework.util.ReflectionUtils.*;

public class ProcurementMilestonesSpendProfileFigureDistributerTest {

    @Test
    public void testMilestoneTotalsPerMonthDurationTooShort(){
        // Setup
        int durationOfProject = 5;
        List<ProjectProcurementMilestoneResource> durationNotTooShortForMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(durationOfProject).withPayment(valueOf(10)).build()
        );
        // Method under test
        callMilestoneTotalsPerMonth(durationOfProject , durationNotTooShortForMilestones); // Success if this does not throw.

        // Setup
        List<ProjectProcurementMilestoneResource> durationTooShortForMilestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(durationOfProject + 1).withPayment(valueOf(10)).build()
        );

        // Method under test
         assertThrows("We should get an illegal state exception if the duration is not long enough for the milestones",
                 IllegalStateException.class,
                 () -> callMilestoneTotalsPerMonth(durationOfProject, durationTooShortForMilestones));
    }

    @Test
    public void testMilestoneTotalsPerMonth(){
        // Setup
        List<ProjectProcurementMilestoneResource> milestones = asList(
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(1).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(5).withPayment(valueOf(10)).build(),
                newProjectProcurementMilestoneResource().withMonth(6).withPayment(valueOf(15)).build()
        );
        List<BigInteger> expected = asList(
                valueOf(20), // Month 1 (two milestones)
                valueOf(0),
                valueOf(0),
                valueOf(0),
                valueOf(10), // Month 5
                valueOf(15), // Month 6
                valueOf(0) // Project duration is 7 months
        );

        // Method under test
        assertEquals(expected, callMilestoneTotalsPerMonth(7, milestones));

    }

    @Test
    public void testBreakoutCosts(){
        // Setup
        BigDecimal vatRateNotZero = new BigDecimal("0.2");
        List<BigInteger> milestoneTotalsPerMonth = asList(
                valueOf(0),
                valueOf(12),
                valueOf(10),
                valueOf(14),
                valueOf(9)
        );
        List<OtherAndVat> expectedWhenVatRateNotZero = asList(
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(2)), // 10, 2
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(2)), // 8.333..., 1.666...
                new OtherAndVat().withOtherCost(valueOf(12)).withVat(valueOf(2)), // 11.666..., 2.333...
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(1)) // 7.5, 2.5
        );
        List<OtherAndVat> expectedWhenVatRateZero = asList(
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(12)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(14)).withVat(valueOf(0)),
                new OtherAndVat().withOtherCost(valueOf(9)).withVat(valueOf(0))
        );
        // Method under test
        assertEquals(expectedWhenVatRateNotZero, callBreakoutCosts(milestoneTotalsPerMonth, vatRateNotZero));
        assertEquals(expectedWhenVatRateZero, callBreakoutCosts(milestoneTotalsPerMonth, BigDecimal.ZERO));
    }


    @Test
    public void testToCosts(){
        List<OtherAndVat> adjustedCosts = asList(
                new OtherAndVat().withOtherCost(valueOf(10)).withVat(valueOf(2)),
                new OtherAndVat().withOtherCost(valueOf(8)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(7)).withVat(valueOf(1)),
                new OtherAndVat().withOtherCost(valueOf(12)).withVat(valueOf(3)),
                new OtherAndVat().withOtherCost(valueOf(0)).withVat(valueOf(0))
        );
        // Method under test
        List<List<Cost>> costs = callToCosts(
                adjustedCosts,
                newCostCategory().withName(OTHER_COSTS.getDisplayName()).build(),
                newCostCategory().withName(VAT.getDisplayName()).build()
        );
        // Assertions
        Assert.assertEquals(2, costs.size());
        Assert.assertEquals(5, costs.get(0).size());
        Assert.assertEquals(5, costs.get(1).size());

        List<Cost> otherCosts = costs.stream().filter(costsList-> costsList.get(0).getCostCategory().getName().equals(OTHER_COSTS.getDisplayName())).findFirst().get();
        List<Cost> vatCosts = costs.stream().filter(costsList-> costsList.get(0).getCostCategory().getName().equals(VAT.getDisplayName())).findFirst().get();

        Assert.assertEquals(new BigDecimal("10"), otherCosts.get(0).getValue());
        Assert.assertEquals(new BigDecimal("8"), otherCosts.get(1).getValue());
        Assert.assertEquals(new BigDecimal("7"), otherCosts.get(2).getValue());
        Assert.assertEquals(new BigDecimal("12"), otherCosts.get(3).getValue());
        Assert.assertEquals(new BigDecimal("0"), otherCosts.get(4).getValue());

        Assert.assertEquals(new BigDecimal("2"), vatCosts.get(0).getValue());
        Assert.assertEquals(new BigDecimal("1"), vatCosts.get(1).getValue());
        Assert.assertEquals(new BigDecimal("1"), vatCosts.get(2).getValue());
        Assert.assertEquals(new BigDecimal("3"), vatCosts.get(3).getValue());
        Assert.assertEquals(new BigDecimal("0"), vatCosts.get(4).getValue());
    }

    private List<List<Cost>> callToCosts(List<OtherAndVat> adjustedCosts, CostCategory otherCostCategory, CostCategory vatCostCategory){
        Method toCostsMethod = findMethod(ProcurementMilestonesSpendProfileFigureDistributer.class, "toCosts", List.class, CostCategory.class, CostCategory.class);
        makeAccessible(toCostsMethod);
        return (List<List<Cost>>) invokeMethod(toCostsMethod, new ProcurementMilestonesSpendProfileFigureDistributer(),adjustedCosts, otherCostCategory, vatCostCategory);
    }

    private List<OtherAndVat> callBreakoutCosts(List<BigInteger> milestoneTotalsPerMonth, BigDecimal vatRate){
        Method breakoutCostsMethod = findMethod(ProcurementMilestonesSpendProfileFigureDistributer.class, "breakoutCosts", List.class, BigDecimal.class);
        makeAccessible(breakoutCostsMethod);
        return (List<OtherAndVat>) invokeMethod(breakoutCostsMethod, new ProcurementMilestonesSpendProfileFigureDistributer(), milestoneTotalsPerMonth, vatRate);
    }

    private List<BigInteger> callMilestoneTotalsPerMonth(int duration, List<ProjectProcurementMilestoneResource> milestones){
        Method milestoneTotalsPerMonthMethod= findMethod(ProcurementMilestonesSpendProfileFigureDistributer.class, "milestoneTotalsPerMonth", int.class, List.class);
        makeAccessible(milestoneTotalsPerMonthMethod);
        return (List<BigInteger>) invokeMethod(milestoneTotalsPerMonthMethod, new ProcurementMilestonesSpendProfileFigureDistributer(), duration, milestones);
    }

}
