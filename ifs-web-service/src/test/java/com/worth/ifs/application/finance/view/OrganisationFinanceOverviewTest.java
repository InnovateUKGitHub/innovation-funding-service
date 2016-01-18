package com.worth.ifs.application.finance.view;

import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.application.finance.builder.OrganisationFinanceOverviewBuilder.newOrganisationFinanceOverviewBuilder;

import static org.mockito.Mockito.when;

public class OrganisationFinanceOverviewTest {
    OrganisationFinanceOverview organisationFinanceOverview;

    @Mock
    OrganisationFinance organisationFinance1;

    @Mock
    OrganisationFinance organisationFinance2;


    @Before
    public void setUp() throws Exception {
        organisationFinance1 = new OrganisationFinance(1L, new Organisation(), new ArrayList<Cost>());
        organisationFinance2 = new OrganisationFinance(2L, new Organisation(), new ArrayList<Cost>());

        MockitoAnnotations.initMocks(this);

        List<OrganisationFinance> organisationFinanceList = new ArrayList<OrganisationFinance>();
        organisationFinanceList.add(organisationFinance1);
        organisationFinanceList.add(organisationFinance2);

        organisationFinanceOverview = newOrganisationFinanceOverviewBuilder().withOrganisationFinances(organisationFinanceList).build();
    }

    @Test
    public void costCalculationsShouldReturnZeroWhenThereAreNoOrganisationFinances() throws Exception {
        OrganisationFinanceOverview organisationFinanceOverview = new OrganisationFinanceOverview();
        Assert.assertEquals(organisationFinanceOverview.getTotal(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalContribution(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalOtherFunding(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalFundingSought(), new BigDecimal(0));
    }

    @Test
    public void totalCostsShouldBeAllOrganisationCostsAddedTogether () throws Exception {
        BigDecimal totalValue1 = new BigDecimal(123);
        BigDecimal totalValue2 = new BigDecimal(456);

        when(organisationFinance1.getTotal()).thenReturn(totalValue1);
        when(organisationFinance2.getTotal()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotal(), totalValue1.add(totalValue2));

    }

    @Test
    public void totalFundingSoughtShouldBeAllOrganisationFundingAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(124);
        BigDecimal totalValue2 = new BigDecimal(457);

        when(organisationFinance1.getTotalFundingSought()).thenReturn(totalValue1);
        when(organisationFinance2.getTotalFundingSought()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalFundingSought(), totalValue1.add(totalValue2));
    }

    @Test
    public void totalContributionShouldBeAllOrganisationContributionsAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(125);
        BigDecimal totalValue2 = new BigDecimal(458);

        when(organisationFinance1.getTotalContribution()).thenReturn(totalValue1);
        when(organisationFinance2.getTotalContribution()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalContribution(), totalValue1.add(totalValue2));
    }

    @Test
    public void totalOtherFundingShouldBeAllOrganisationOtherFundingAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(126);
        BigDecimal totalValue2 = new BigDecimal(459);

        when(organisationFinance1.getTotalOtherFunding()).thenReturn(totalValue1);
        when(organisationFinance2.getTotalOtherFunding()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalOtherFunding(), totalValue1.add(totalValue2));
    }
}