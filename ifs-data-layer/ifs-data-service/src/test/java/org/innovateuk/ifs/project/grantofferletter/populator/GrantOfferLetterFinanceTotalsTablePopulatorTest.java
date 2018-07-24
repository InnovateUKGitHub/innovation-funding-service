package org.innovateuk.ifs.project.grantofferletter.populator;


import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.finance.domain.ProjectFinanceRow;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRepository;
import org.innovateuk.ifs.finance.repository.ProjectFinanceRowRepository;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeEnum;
import org.innovateuk.ifs.project.financechecks.domain.Cost;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTable;
import org.innovateuk.ifs.project.grantofferletter.model.GrantOfferLetterFinanceTotalsTablePopulator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


import java.math.BigDecimal;
import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceRowBuilder.newProjectFinanceRow;
import static org.innovateuk.ifs.finance.domain.builder.ProjectFinanceBuilder.newProjectFinance;
import static org.innovateuk.ifs.organisation.builder.OrganisationBuilder.newOrganisation;
import static org.innovateuk.ifs.project.financecheck.builder.CostBuilder.newCost;
import static org.innovateuk.ifs.project.financecheck.builder.CostCategoryBuilder.newCostCategory;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GrantOfferLetterFinanceTotalsTablePopulatorTest {

    @InjectMocks
    private GrantOfferLetterFinanceTotalsTablePopulator populator;

    @Mock
    private ProjectFinanceRepository projectFinanceRepositoryMock;

    @Mock
    private ProjectFinanceRowRepository projectFinanceRowRepositoryMock;

    private Organisation organisation1;
    private Organisation organisation2;
    private Organisation organisation3;
    private Organisation organisation4;

    private Cost cost1;
    private Cost cost2;
    private Cost cost3;
    private Cost cost4;
    private Cost cost5;
    private Cost cost6;
    private Cost cost7;
    private Cost cost8;

    @Before
    public void setUp() {
        organisation1 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .withName("Org1")
                .build();

        organisation2 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.BUSINESS)
                .withName("Org2")
                .build();

        organisation3 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("Org3")
                .build();

        organisation4 = newOrganisation()
                .withOrganisationType(OrganisationTypeEnum.RESEARCH)
                .withName("Org4")
                .build();

        cost1 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Materials")
                                          .build())
                .withValue(BigDecimal.valueOf(67))
                .build();

        cost2 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Labour")
                                          .build())
                .withValue(BigDecimal.valueOf(42))
                .build();

        cost3 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Subcontracting")
                                          .build())
                .withValue(BigDecimal.valueOf(345))
                .build();

        cost4 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Other costs")
                                          .build())
                .withValue(BigDecimal.valueOf(89))
                .build();

        cost5 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Exceptions")
                                          .build())
                .withValue(BigDecimal.valueOf(21))
                .build();

        cost6 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Travel and subsistence")
                                          .build())
                .withValue(BigDecimal.valueOf(63))
                .build();

        cost7 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Staff")
                                          .build())
                .withValue(BigDecimal.valueOf(906))
                .build();

        cost8 = newCost()
                .withCostCategory(newCostCategory()
                                          .withName("Equipment")
                                          .build())
                .withValue(BigDecimal.valueOf(8))
                .build();

        ProjectFinance pf = newProjectFinance().build();

        ProjectFinanceRow pfr = newProjectFinanceRow().withQuantity(30)
                .withName("grant-claim")
                .build();

        when(projectFinanceRepositoryMock.findByProjectIdAndOrganisationId(anyLong(), anyLong())).thenReturn(pf);
        when(projectFinanceRowRepositoryMock.findByTargetId(pf.getId())).thenReturn(singletonList(pfr));

    }

    @Test
    public void createTable() {

        Long projectId = 657L;
        Map<Organisation, List<Cost>> finances = new HashMap<>();
        finances.put(organisation1, asList(cost1, cost2));
        finances.put(organisation2, asList(cost3, cost4));
        finances.put(organisation3, asList(cost5, cost6));
        finances.put(organisation4, asList(cost7, cost8));

        GrantOfferLetterFinanceTotalsTable table = populator.createTable(finances, projectId);

        assertEquals(BigDecimal.valueOf(30), table.getGrantClaim(organisation1.getName()));
        assertEquals(BigDecimal.valueOf(100), table.getGrantClaim(organisation3.getName()));
        assertEquals( BigDecimal.valueOf(434), table.getTotalEligibleCosts(organisation2.getName()));
        assertEquals(BigDecimal.valueOf(914), table.getTotalEligibleCosts(organisation4.getName()));
        assertEquals( new BigDecimal("32.70"), table.getTotalGrant(organisation1.getName()));
        assertEquals(new BigDecimal("84.00"), table.getTotalGrant(organisation3.getName()));
        assertEquals(new BigDecimal("543"), table.getIndustryTotalEligibleCosts());
        assertEquals(new BigDecimal("998"), table.getAcademicTotalEligibleCosts());
        assertEquals(new BigDecimal("1541"), table.getAllTotalEligibleCosts());
        assertEquals(new BigDecimal("162.90"), table.getIndustryTotalGrant());
        assertEquals(new BigDecimal("998.00"), table.getAcademicTotalGrant());
        assertEquals(new BigDecimal("1160.90"), table.getAllTotalGrant());
        assertEquals(new BigDecimal("30.00"), table.getIndustryTotalGrantClaim());
        assertEquals(new BigDecimal("100"), table.getAcademicTotalGrantClaim());
        assertEquals(new BigDecimal("75.00"), table.getAllTotalGrantClaim());

        assertTrue(table.getIndustrialOrgs().contains(organisation1.getName()));
        assertTrue(table.getIndustrialOrgs().contains(organisation2.getName()));
        assertTrue(table.getAcademicOrgs().contains(organisation3.getName()));
        assertTrue(table.getAcademicOrgs().contains(organisation4.getName()));

        // verifying that both mocks are invoked, but only for the industrial organisations
        verify(projectFinanceRepositoryMock, times(2)).findByProjectIdAndOrganisationId(anyLong(), anyLong());
        verify(projectFinanceRowRepositoryMock, times(2)).findByTargetId(anyLong());
    }

}
