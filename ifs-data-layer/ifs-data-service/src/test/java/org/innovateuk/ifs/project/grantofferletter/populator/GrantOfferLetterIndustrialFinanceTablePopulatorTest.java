package org.innovateuk.ifs.project.grantofferletter.populator;

import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterIndustrialFinanceTable;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterIndustrialFinanceTablePopulator;
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
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterIndustrialFinanceTablePopulatorTest {

    @InjectMocks
    private GrantOfferLetterIndustrialFinanceTablePopulator populator;

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

    @Before
    public void setup() {
        organisation1 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .withName("org1")
                .build();

        organisation2 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .withName("org2")
                .build();

        cost1 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Labour")
                                          .build())
                .withValue(BigDecimal.valueOf(58))
                .build();

        cost2 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Materials")
                                          .build())
                .withValue(BigDecimal.valueOf(22))
                .build();

        cost3 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Overheads")
                                          .build())
                .withValue(BigDecimal.valueOf(11))
                .build();

        cost4 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Capital usage")
                                          .build())
                .withValue(BigDecimal.valueOf(31))
                .build();

        cost5 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Subcontracting")
                                          .build())
                .withValue(BigDecimal.valueOf(45))
                .build();

        cost6 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Subcontracting")
                                          .build())
                .withValue(BigDecimal.valueOf(65))
                .build();

        cost7 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Subcontracting")
                                          .build())
                .withValue(BigDecimal.valueOf(65))
                .build();

        cost8 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Travel and subsistence")
                                          .build())
                .withValue(BigDecimal.valueOf(31))
                .build();

        cost9 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Travel and subsistence")
                                          .build())
                .withValue(BigDecimal.valueOf(12))
                .build();

        cost10 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Other costs")
                                          .build())
                .withValue(BigDecimal.valueOf(89))
                .build();

        cost11 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Other costs")
                                          .build())
                .withValue(BigDecimal.valueOf(97))
                .build();
    }

    @Test
    public void createTable() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        finances.put(organisation1, asList(cost1, cost2, cost3, cost4, cost5, cost6, cost8, cost9, cost10));
        finances.put(organisation2, asList(cost1, cost2, cost4, cost7, cost9, cost11));
        GrantOfferLetterIndustrialFinanceTable table = populator.createTable(finances);

        assertEquals(BigDecimal.valueOf(58), table.getLabour(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(58), table.getLabour(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(22), table.getMaterials(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(22), table.getMaterials(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(11), table.getOverheads(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(0), table.getOverheads(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(31), table.getCapitalUsage(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(31), table.getCapitalUsage(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(110), table.getSubcontract(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(65), table.getSubcontract(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(43), table.getTravel(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(12), table.getTravel(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(89), table.getOtherCosts(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(97), table.getOtherCosts(organisation2.getName()));

        assertEquals(BigDecimal.valueOf(116), table.getLabourTotal());
        assertEquals(BigDecimal.valueOf(44), table.getMaterialsTotal());
        assertEquals(BigDecimal.valueOf(11), table.getOverheadsTotal());
        assertEquals(BigDecimal.valueOf(62), table.getCapitalUsageTotal());
        assertEquals(BigDecimal.valueOf(175), table.getSubcontractTotal());
        assertEquals(BigDecimal.valueOf(55), table.getTravelTotal());
        assertEquals(BigDecimal.valueOf(186), table.getOtherCostsTotal());

        assertTrue(table.getOrganisations().contains(organisation1.getName()));
        assertTrue(table.getOrganisations().contains(organisation2.getName()));

    }

    @Test
    public void tableNullIfNoOrganisations() {
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        GrantOfferLetterIndustrialFinanceTable table = populator.createTable(finances);
        assertTrue(table == null);
    }

}
