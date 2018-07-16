package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterAcademicFinanceTable;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterAcademicFinanceTablePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.finance.resource.cost.AcademicCostCategoryGenerator.*;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterAcademicFinanceTablePopulatorTest {

    @InjectMocks
    private GrantOfferLetterAcademicFinanceTablePopulator populator;

    private Organisation organisation1;
    private Organisation organisation2;
    private Cost cost1;
    private Cost cost2;
    private Cost cost3;
    private Cost cost4;
    private Cost cost5;
    private Cost cost6;
    private Cost cost7;
    private Cost cost8;
    private Cost cost9;
    private Cost cost10;
    private Cost cost11;
    private Cost cost12;
    private Cost cost13;
    private Cost cost14;
    private Cost cost15;
    private Cost cost16;
    private Cost cost17;
    private Cost cost18;
    private Cost cost19;
    private Cost cost20;

    @Before
    public void setUp() {
        organisation1 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("org1")
                .build();

        organisation2 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("org2")
                .build();

        cost1 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_STAFF.getName())
                                          .withLabel(DIRECTLY_INCURRED_STAFF.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(11))
                .build();

        cost2 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_STAFF.getName())
                                          .withLabel(DIRECTLY_INCURRED_STAFF.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(31))
                .build();


        cost3 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(45))
                .build();

        cost4 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(22))
                .build();

        cost5 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_EQUIPMENT.getName())
                                          .withLabel(DIRECTLY_INCURRED_EQUIPMENT.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(54))
                .build();

        cost6 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_OTHER_COSTS.getName())
                                          .withLabel(DIRECTLY_INCURRED_OTHER_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(34))
                .build();

        cost7 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_OTHER_COSTS.getName())
                                          .withLabel(DIRECTLY_INCURRED_OTHER_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(58))
                .build();

        cost8 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_ALLOCATED_INVESTIGATORS.getName())
                                          .withLabel(DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(106))
                .build();

        cost9 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_ALLOCATED_INVESTIGATORS.getName())
                                          .withLabel(DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(306))
                .build();


        cost10 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_ALLOCATED_ESTATES_COSTS.getName())
                                          .withLabel(DIRECTLY_ALLOCATED_ESTATES_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(456))
                .build();

        cost11 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_ALLOCATED_OTHER_COSTS.getName())
                                          .withLabel(DIRECTLY_ALLOCATED_OTHER_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(45))
                .build();

        cost12 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_ALLOCATED_OTHER_COSTS.getName())
                                          .withLabel(DIRECTLY_ALLOCATED_OTHER_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(3))
                .build();

        cost13 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS.getName())
                                          .withLabel(INDIRECT_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(44))
                .build();

        cost14 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_STAFF.getName())
                                          .withLabel(INDIRECT_COSTS_STAFF.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(39))
                .build();

        cost15 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_STAFF.getName())
                                          .withLabel(INDIRECT_COSTS_STAFF.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(905))
                .build();

        cost16 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(78))
                .build();

        cost17 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(43))
                .build();

        cost18 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_EQUIPMENT.getName())
                                          .withLabel(INDIRECT_COSTS_EQUIPMENT.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(432))
                .build();

        cost19 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_EQUIPMENT.getName())
                                          .withLabel(INDIRECT_COSTS_EQUIPMENT.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(234))
                .build();

        cost20 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(INDIRECT_COSTS_OTHER_COSTS.getName())
                                          .withLabel(INDIRECT_COSTS_OTHER_COSTS.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(82))
                .build();
    }

    @Test
    public void createTable() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        finances.put(organisation1, asList(cost1, cost2, cost3, cost4, cost5, cost6, cost8, cost9, cost10, cost12, cost13, cost15, cost17, cost19));
        finances.put(organisation2, asList(cost1, cost2, cost4, cost7, cost9, cost11, cost13, cost14, cost16, cost18, cost20));

        GrantOfferLetterAcademicFinanceTable table = populator.createTable(finances);

        assertEquals(BigDecimal.valueOf(42), table.getIncurredStaff(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(42), table.getIncurredStaff(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(67), table.getIncurredTravelSubsistence(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(22), table.getIncurredTravelSubsistence(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(54), table.getIncurredEquipment(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(0), table.getIncurredEquipment(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(34), table.getIncurredOtherCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(58), table.getIncurredOtherCosts(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(412), table.getAllocatedInvestigators(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(306), table.getAllocatedInvestigators(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(456), table.getAllocatedEstateCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(0), table.getAllocatedEstateCosts(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(34), table.getAllocatedOtherCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(58), table.getAllocatedOtherCosts(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(0), table.getIndirectCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(82), table.getIndirectCosts(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(905), table.getExceptionsStaff(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(39), table.getExceptionsStaff(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(43), table.getExceptionsTravelSubsistence(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(78), table.getExceptionsTravelSubsistence(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(234), table.getExceptionsEquipment(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(432), table.getExceptionsEquipment(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(0), table.getExceptionsOtherCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(82), table.getExceptionsOtherCosts(organisation2.getName()));

        assertEquals(BigDecimal.valueOf(84), table.getIncurredStaffTotal());
        assertEquals(BigDecimal.valueOf(89), table.getIncurredTravelSubsistenceTotal());
        assertEquals(BigDecimal.valueOf(54), table.getIncurredEquipmentTotal());
        assertEquals(BigDecimal.valueOf(92), table.getIncurredOtherCostsTotal());
        assertEquals(BigDecimal.valueOf(718), table.getAllocatedInvestigatorsTotal());
        assertEquals(BigDecimal.valueOf(456), table.getAllocatedEstateCostsTotal());
        assertEquals(BigDecimal.valueOf(92), table.getAllocatedOtherCostsTotal());
        assertEquals(BigDecimal.valueOf(82), table.getIndirectCostsTotal());
        assertEquals(BigDecimal.valueOf(944), table.getExceptionsStaffTotal());
        assertEquals(BigDecimal.valueOf(121), table.getExceptionsTravelSubsistenceTotal());
        assertEquals(BigDecimal.valueOf(666), table.getExceptionsEquipmentTotal());
        assertEquals(BigDecimal.valueOf(82), table.getExceptionsOtherCostsTotal());

        assertTrue(table.getOrganisations().contains(organisation1.getName()));
        assertTrue(table.getOrganisations().contains(organisation2.getName()));
    }

    @Test
    public void tableNullIfNoOrganisations() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        GrantOfferLetterAcademicFinanceTable table = populator.createTable(finances);
        assertTrue(table == null);
    }

}
