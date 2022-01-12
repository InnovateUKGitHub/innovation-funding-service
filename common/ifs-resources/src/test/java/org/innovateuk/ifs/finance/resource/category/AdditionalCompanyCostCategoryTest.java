package org.innovateuk.ifs.finance.resource.category;

import org.innovateuk.ifs.finance.resource.cost.AdditionalCompanyCost;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowItem;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.builder.AdditionalCompanyCostBuilder.newAdditionalCompanyCost;
import static org.innovateuk.ifs.finance.builder.AdditionalCompanyCostCategoryBuilder.newAdditionalCompanyCostCategory;
import static org.junit.Assert.*;

public class AdditionalCompanyCostCategoryTest {

    private List<FinanceRowItem> costs = new ArrayList<>();
    private AdditionalCompanyCost associateSalary;
    private AdditionalCompanyCost managementSupervision;
    private AdditionalCompanyCost otherStaff;
    private AdditionalCompanyCost capitalEquipment;
    private AdditionalCompanyCost consumables;
    private AdditionalCompanyCost otherCosts;
    private AdditionalCompanyCostCategory additionalCompanyCostCategory;

    @Before
    public void setUp() throws Exception {
        associateSalary = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.ASSOCIATE_SALARY)
                .withDescription("associateSalary")
                .withCost(BigInteger.valueOf(100))
                .build();
        costs.add(associateSalary);

        managementSupervision = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.MANAGEMENT_SUPERVISION)
                .withDescription("managementSupervision")
                .withCost(BigInteger.valueOf(200))
                .build();
        costs.add(managementSupervision);

        otherStaff = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.OTHER_STAFF)
                .withDescription("otherStaff")
                .withCost(BigInteger.valueOf(300))
                .build();
        costs.add(otherStaff);

        capitalEquipment = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.CAPITAL_EQUIPMENT)
                .withDescription("capitalEquipment")
                .withCost(BigInteger.valueOf(400))
                .build();
        costs.add(capitalEquipment);

        consumables = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.CONSUMABLES)
                .withDescription("Consumables")
                .withCost(BigInteger.valueOf(500))
                .build();
        costs.add(consumables);

        otherCosts = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.OTHER_COSTS)
                .withDescription("otherCosts")
                .withCost(BigInteger.valueOf(600))
                .build();
        costs.add(otherCosts);

        additionalCompanyCostCategory = newAdditionalCompanyCostCategory()
                .withCosts(asList(associateSalary, managementSupervision, otherStaff, capitalEquipment, consumables, otherCosts))
                .build();
    }

    @Test
    public void getAssociateSalary() {
        assertEquals(associateSalary, additionalCompanyCostCategory.getAssociateSalary());
    }

    @Test
    public void getManagementSupervision() {
        assertEquals(managementSupervision, additionalCompanyCostCategory.getManagementSupervision());
    }

    @Test
    public void getOtherStaff() {
        assertEquals(otherStaff, additionalCompanyCostCategory.getOtherStaff());
    }

    @Test
    public void getCapitalEquipment() {
        assertEquals(capitalEquipment, additionalCompanyCostCategory.getCapitalEquipment());
    }

    @Test
    public void getConsumables() {
        assertEquals(consumables, additionalCompanyCostCategory.getConsumables());
    }

    @Test
    public void getOtherCosts() {
        assertEquals(otherCosts, additionalCompanyCostCategory.getOtherCosts());
    }

    @Test
    public void getCosts() {
        assertEquals(costs, additionalCompanyCostCategory.getCosts());
    }

    @Test
    public void getTotal() {
        BigDecimal result = associateSalary.getTotal()
                .add(managementSupervision.getTotal())
                .add(otherStaff.getTotal())
                .add(capitalEquipment.getTotal())
                .add(consumables.getTotal())
                .add(otherCosts.getTotal());
        additionalCompanyCostCategory.calculateTotal();

        assertEquals(result, additionalCompanyCostCategory.getTotal());
    }

    @Test
    public void addCost() {
        AdditionalCompanyCost newConsumables = newAdditionalCompanyCost()
                .withType(AdditionalCompanyCost.AdditionalCompanyCostType.CONSUMABLES)
                .withDescription("newAdditionalCompanyCost")
                .withCost(BigInteger.valueOf(700))
                .build();

        BigDecimal newResult = associateSalary.getTotal()
                .add(managementSupervision.getTotal())
                .add(otherStaff.getTotal())
                .add(capitalEquipment.getTotal())
                .add(newConsumables.getTotal())
                .add(otherCosts.getTotal());

        additionalCompanyCostCategory.addCost(newConsumables);
        assertEquals(newConsumables, additionalCompanyCostCategory.getConsumables());

        additionalCompanyCostCategory.calculateTotal();
        assertEquals(newResult, additionalCompanyCostCategory.getTotal());
    }

    @Test
    public void checkCostCategoryNotToSetToExcludeFromTotalCosts() {
        assertTrue(additionalCompanyCostCategory.excludeFromTotalCost());
    }
}