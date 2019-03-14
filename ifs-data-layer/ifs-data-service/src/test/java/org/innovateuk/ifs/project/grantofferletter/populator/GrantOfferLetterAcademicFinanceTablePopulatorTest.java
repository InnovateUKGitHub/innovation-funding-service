package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterAcademicFinanceTable;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterAcademicFinanceTablePopulator;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

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

@RunWith(MockitoJUnitRunner.Silent.class)
public class GrantOfferLetterAcademicFinanceTablePopulatorTest {

    @InjectMocks
    private GrantOfferLetterAcademicFinanceTablePopulator populator;

    private static Organisation ORGANISATION_1;
    private static Organisation ORGANISATION_2;
    private static Cost DIRECTLY_INCURRED_STAFF_2;
    private static Cost DIRECTLY_INCURRED_STAFF_3;
    private static Cost DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_5;
    private static Cost DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_7;
    private static Cost DIRECTLY_INCURRED_EQUIPMENT_11;
    private static Cost DIRECTLY_INCURRED_OTHER_COSTS_13;
    private static Cost DIRECTLY_INCURRED_OTHER_COSTS_17;
    private static Cost DIRECTLY_ALLOCATED_INVESTIGATORS_19;
    private static Cost DIRECTLY_ALLOCATED_INVESTIGATORS_23;
    private static Cost DIRECTLY_ALLOCATED_ESTATES_COSTS_29;
    private static Cost DIRECTLY_ALLOCATED_OTHER_COSTS_31;
    private static Cost DIRECTLY_ALLOCATED_OTHER_COSTS_37;
    private static Cost INDIRECT_COSTS_41;
    private static Cost INDIRECT_COSTS_STAFF_43;
    private static Cost INDIRECT_COSTS_STAFF_47;
    private static Cost INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_53;
    private static Cost INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_59;
    private static Cost INDIRECT_COSTS_EQUIPMENT_61;
    private static Cost INDIRECT_COSTS_EQUIPMENT_67;
    private static Cost INDIRECT_COSTS_OTHER_COSTS_71;
    private static Map<Organisation, List<Cost>> FINANCES = new HashMap<>();

    @BeforeClass
    public static void setUp() {
        doSetUp();
    }

    @Test
    public void getIncurredStaff() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(5), grantOfferLetterAcademicFinanceTable.getIncurredStaff(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(5), grantOfferLetterAcademicFinanceTable.getIncurredStaff(ORGANISATION_2.getName()));
    }

    @Test
    public void getIncurredTravelSubsistence() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(12), grantOfferLetterAcademicFinanceTable.getIncurredTravelSubsistence(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(7), grantOfferLetterAcademicFinanceTable.getIncurredTravelSubsistence(ORGANISATION_2.getName()));
    }

    @Test
    public void getIncurredEquipment() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(11), grantOfferLetterAcademicFinanceTable.getIncurredEquipment(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(0), grantOfferLetterAcademicFinanceTable.getIncurredEquipment(ORGANISATION_2.getName()));
    }

    @Test
    public void getIncurredOtherCosts() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(13), grantOfferLetterAcademicFinanceTable.getIncurredOtherCosts(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(17), grantOfferLetterAcademicFinanceTable.getIncurredOtherCosts(ORGANISATION_2.getName()));
    }

    @Test
    public void getAllocatedInvestigators() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(42), grantOfferLetterAcademicFinanceTable.getAllocatedInvestigators(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(23), grantOfferLetterAcademicFinanceTable.getAllocatedInvestigators(ORGANISATION_2.getName()));
    }

    @Test
    public void getAllocatedEstateCosts() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(29), grantOfferLetterAcademicFinanceTable.getAllocatedEstateCosts(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(0), grantOfferLetterAcademicFinanceTable.getAllocatedEstateCosts(ORGANISATION_2.getName()));
    }

    @Test
    public void getAllocatedOtherCosts() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(13), grantOfferLetterAcademicFinanceTable.getAllocatedOtherCosts(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(17), grantOfferLetterAcademicFinanceTable.getAllocatedOtherCosts(ORGANISATION_2.getName()));
    }

    @Test
    public void getIndirectCosts() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(0), grantOfferLetterAcademicFinanceTable.getIndirectCosts(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(71), grantOfferLetterAcademicFinanceTable.getIndirectCosts(ORGANISATION_2.getName()));
    }

    @Test
    public void getExceptionsStaff() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(47), grantOfferLetterAcademicFinanceTable.getExceptionsStaff(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(43), grantOfferLetterAcademicFinanceTable.getExceptionsStaff(ORGANISATION_2.getName()));
    }

    @Test
    public void getExceptionsTravelSubsistence() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(59), grantOfferLetterAcademicFinanceTable.getExceptionsTravelSubsistence(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(53), grantOfferLetterAcademicFinanceTable.getExceptionsTravelSubsistence(ORGANISATION_2.getName()));
    }

    @Test
    public void getExceptionsOtherCosts() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(0), grantOfferLetterAcademicFinanceTable.getExceptionsOtherCosts(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(71), grantOfferLetterAcademicFinanceTable.getExceptionsOtherCosts(ORGANISATION_2.getName()));
    }

    @Test
    public void getOrganisations() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertTrue(grantOfferLetterAcademicFinanceTable.getOrganisations().contains(ORGANISATION_1.getName()));
        assertTrue(grantOfferLetterAcademicFinanceTable.getOrganisations().contains(ORGANISATION_2.getName()));
    }

    @Test
    public void getTotals() {
        GrantOfferLetterAcademicFinanceTable grantOfferLetterAcademicFinanceTable = populator.createTable(FINANCES);

        assertEquals(BigDecimal.valueOf(10), grantOfferLetterAcademicFinanceTable.getIncurredStaffTotal());
        assertEquals(BigDecimal.valueOf(19), grantOfferLetterAcademicFinanceTable.getIncurredTravelSubsistenceTotal());
        assertEquals(BigDecimal.valueOf(11), grantOfferLetterAcademicFinanceTable.getIncurredEquipmentTotal());
        assertEquals(BigDecimal.valueOf(30), grantOfferLetterAcademicFinanceTable.getIncurredOtherCostsTotal());
        assertEquals(BigDecimal.valueOf(65), grantOfferLetterAcademicFinanceTable.getAllocatedInvestigatorsTotal());
        assertEquals(BigDecimal.valueOf(29), grantOfferLetterAcademicFinanceTable.getAllocatedEstateCostsTotal());
        assertEquals(BigDecimal.valueOf(30), grantOfferLetterAcademicFinanceTable.getAllocatedOtherCostsTotal());
        assertEquals(BigDecimal.valueOf(71), grantOfferLetterAcademicFinanceTable.getIndirectCostsTotal());
        assertEquals(BigDecimal.valueOf(90), grantOfferLetterAcademicFinanceTable.getExceptionsStaffTotal());
        assertEquals(BigDecimal.valueOf(112), grantOfferLetterAcademicFinanceTable.getExceptionsTravelSubsistenceTotal());
        assertEquals(BigDecimal.valueOf(128), grantOfferLetterAcademicFinanceTable.getExceptionsEquipmentTotal());
        assertEquals(BigDecimal.valueOf(71), grantOfferLetterAcademicFinanceTable.getExceptionsOtherCostsTotal());
    }

    @Test
    public void tableNullIfNoOrganisations() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        GrantOfferLetterAcademicFinanceTable table = populator.createTable(finances);
        assertTrue(table == null);
    }

    @Test
    public void nullCostValuesHandledGracefully() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();

        BigDecimal nullTest = null;
        DIRECTLY_INCURRED_STAFF_2 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(nullTest)
                .build();

        DIRECTLY_INCURRED_STAFF_3 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                                          .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                                          .build())
                .withValue(BigDecimal.valueOf(58))
                .build();

        finances.put(ORGANISATION_1, asList(DIRECTLY_INCURRED_STAFF_2, DIRECTLY_INCURRED_STAFF_3));
        GrantOfferLetterAcademicFinanceTable table = populator.createTable(finances);

        assertEquals(BigDecimal.valueOf(58), table.getIncurredTravelSubsistence(ORGANISATION_1.getName()));
        assertEquals(BigDecimal.valueOf(58), table.getIncurredTravelSubsistenceTotal());
    }

    private static void doSetUp() {
        ORGANISATION_1 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("org1")
                .build();

        ORGANISATION_2 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("org2")
                .build();

        DIRECTLY_INCURRED_STAFF_2 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_STAFF.getName())
                        .withLabel(DIRECTLY_INCURRED_STAFF.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(2))
                .build();

        DIRECTLY_INCURRED_STAFF_3 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_STAFF.getName())
                        .withLabel(DIRECTLY_INCURRED_STAFF.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(3))
                .build();


        DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_5 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                        .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(5))
                .build();

        DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_7 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getName())
                        .withLabel(DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(7))
                .build();

        DIRECTLY_INCURRED_EQUIPMENT_11 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_EQUIPMENT.getName())
                        .withLabel(DIRECTLY_INCURRED_EQUIPMENT.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(11))
                .build();

        DIRECTLY_INCURRED_OTHER_COSTS_13 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_OTHER_COSTS.getName())
                        .withLabel(DIRECTLY_INCURRED_OTHER_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(13))
                .build();

        DIRECTLY_INCURRED_OTHER_COSTS_17 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_INCURRED_OTHER_COSTS.getName())
                        .withLabel(DIRECTLY_INCURRED_OTHER_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(17))
                .build();

        DIRECTLY_ALLOCATED_INVESTIGATORS_19 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_ALLOCATED_INVESTIGATORS.getName())
                        .withLabel(DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(19))
                .build();

        DIRECTLY_ALLOCATED_INVESTIGATORS_23 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_ALLOCATED_INVESTIGATORS.getName())
                        .withLabel(DIRECTLY_ALLOCATED_INVESTIGATORS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(23))
                .build();


        DIRECTLY_ALLOCATED_ESTATES_COSTS_29 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_ALLOCATED_ESTATES_COSTS.getName())
                        .withLabel(DIRECTLY_ALLOCATED_ESTATES_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(29))
                .build();

        DIRECTLY_ALLOCATED_OTHER_COSTS_31 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_ALLOCATED_OTHER_COSTS.getName())
                        .withLabel(DIRECTLY_ALLOCATED_OTHER_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(31))
                .build();

        DIRECTLY_ALLOCATED_OTHER_COSTS_37 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(DIRECTLY_ALLOCATED_OTHER_COSTS.getName())
                        .withLabel(DIRECTLY_ALLOCATED_OTHER_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(37))
                .build();

        INDIRECT_COSTS_41 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS.getName())
                        .withLabel(INDIRECT_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(41))
                .build();

        INDIRECT_COSTS_STAFF_43 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_STAFF.getName())
                        .withLabel(INDIRECT_COSTS_STAFF.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(43))
                .build();

        INDIRECT_COSTS_STAFF_47 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_STAFF.getName())
                        .withLabel(INDIRECT_COSTS_STAFF.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(47))
                .build();

        INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_53 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName())
                        .withLabel(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(53))
                .build();

        INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_59 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getName())
                        .withLabel(INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(59))
                .build();

        INDIRECT_COSTS_EQUIPMENT_61 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_EQUIPMENT.getName())
                        .withLabel(INDIRECT_COSTS_EQUIPMENT.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(61))
                .build();

        INDIRECT_COSTS_EQUIPMENT_67 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_EQUIPMENT.getName())
                        .withLabel(INDIRECT_COSTS_EQUIPMENT.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(67))
                .build();

        INDIRECT_COSTS_OTHER_COSTS_71 = newCost()
                .withCostCategory(newCostCategory()
                        .withName(INDIRECT_COSTS_OTHER_COSTS.getName())
                        .withLabel(INDIRECT_COSTS_OTHER_COSTS.getLabel())
                        .build())
                .withValue(BigDecimal.valueOf(71))
                .build();

        FINANCES.put(ORGANISATION_1, asList(DIRECTLY_INCURRED_STAFF_2,
                DIRECTLY_INCURRED_STAFF_3,
                DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_5,
                DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_7,
                DIRECTLY_INCURRED_EQUIPMENT_11,
                DIRECTLY_INCURRED_OTHER_COSTS_13,
                DIRECTLY_ALLOCATED_INVESTIGATORS_19,
                DIRECTLY_ALLOCATED_INVESTIGATORS_23,
                DIRECTLY_ALLOCATED_ESTATES_COSTS_29,
                DIRECTLY_ALLOCATED_OTHER_COSTS_37,
                INDIRECT_COSTS_41,
                INDIRECT_COSTS_STAFF_47,
                INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_59,
                INDIRECT_COSTS_EQUIPMENT_67));
        FINANCES.put(ORGANISATION_2, asList(DIRECTLY_INCURRED_STAFF_2,
                DIRECTLY_INCURRED_STAFF_3,
                DIRECTLY_INCURRED_TRAVEL_AND_SUBSISTENCE_7,
                DIRECTLY_INCURRED_OTHER_COSTS_17,
                DIRECTLY_ALLOCATED_INVESTIGATORS_23,
                DIRECTLY_ALLOCATED_OTHER_COSTS_31,
                INDIRECT_COSTS_41,
                INDIRECT_COSTS_STAFF_43,
                INDIRECT_COSTS_TRAVEL_AND_SUBSISTENCE_53,
                INDIRECT_COSTS_EQUIPMENT_61,
                INDIRECT_COSTS_OTHER_COSTS_71));
    }

}
