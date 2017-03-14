package org.innovateuk.ifs.finance.view;

import org.innovateuk.ifs.application.finance.view.OrganisationApplicationFinanceOverviewImpl;
import org.innovateuk.ifs.application.finance.view.OrganisationFinanceOverview;
import org.innovateuk.ifs.finance.builder.OrganisationFinanceOverviewBuilder;
import org.innovateuk.ifs.finance.resource.ApplicationFinanceResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class OrganisationFinanceOverviewTest {
    OrganisationFinanceOverview organisationFinanceOverview;

    @Mock
    ApplicationFinanceResource applicationFinanceResource1;

    @Mock
    ApplicationFinanceResource applicationFinanceResource2;


    @Before
    public void setUp() throws Exception {
        applicationFinanceResource1 = new ApplicationFinanceResource(1L, 1L, 1L, 1L);
        applicationFinanceResource2 = new ApplicationFinanceResource(2L, 2L, 2L, 1L);

        MockitoAnnotations.initMocks(this);

        List<ApplicationFinanceResource> organisationFinanceList = new ArrayList<ApplicationFinanceResource>();
        organisationFinanceList.add(applicationFinanceResource1);
        organisationFinanceList.add(applicationFinanceResource2);

        organisationFinanceOverview = OrganisationFinanceOverviewBuilder.newOrganisationFinanceOverviewBuilder().withOrganisationFinances(organisationFinanceList).build();
    }

    @Test
    public void costCalculationsShouldReturnZeroWhenThereAreNoOrganisationFinances() throws Exception {
        OrganisationApplicationFinanceOverviewImpl organisationFinanceOverview = new OrganisationApplicationFinanceOverviewImpl();
        Assert.assertEquals(organisationFinanceOverview.getTotal(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalContribution(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalOtherFunding(), new BigDecimal(0));
        Assert.assertEquals(organisationFinanceOverview.getTotalFundingSought(), new BigDecimal(0));
    }

    @Test
    public void totalCostsShouldBeAllOrganisationCostsAddedTogether () throws Exception {
        BigDecimal totalValue1 = new BigDecimal(123);
        BigDecimal totalValue2 = new BigDecimal(456);

        when(applicationFinanceResource1.getTotal()).thenReturn(totalValue1);
        when(applicationFinanceResource2.getTotal()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotal(), totalValue1.add(totalValue2));

    }

    @Test
    public void totalFundingSoughtShouldBeAllOrganisationFundingAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(124);
        BigDecimal totalValue2 = new BigDecimal(457);

        when(applicationFinanceResource1.getTotalFundingSought()).thenReturn(totalValue1);
        when(applicationFinanceResource2.getTotalFundingSought()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalFundingSought(), totalValue1.add(totalValue2));
    }

    @Test
    public void totalContributionShouldBeAllOrganisationContributionsAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(125);
        BigDecimal totalValue2 = new BigDecimal(458);

        when(applicationFinanceResource1.getTotalContribution()).thenReturn(totalValue1);
        when(applicationFinanceResource2.getTotalContribution()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalContribution(), totalValue1.add(totalValue2));
    }

    @Test
    public void totalOtherFundingShouldBeAllOrganisationOtherFundingAddedTogether() throws Exception {
        BigDecimal totalValue1 = new BigDecimal(126);
        BigDecimal totalValue2 = new BigDecimal(459);

        when(applicationFinanceResource1.getTotalOtherFunding()).thenReturn(totalValue1);
        when(applicationFinanceResource2.getTotalOtherFunding()).thenReturn(totalValue2);

        Assert.assertEquals(organisationFinanceOverview.getTotalOtherFunding(), totalValue1.add(totalValue2));
    }
}
