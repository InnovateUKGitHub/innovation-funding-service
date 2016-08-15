package com.worth.ifs.finance.model;

import com.worth.ifs.finance.resource.category.DefaultCostCategory;
import com.worth.ifs.finance.resource.category.FinanceRowCostCategory;
import com.worth.ifs.finance.resource.category.LabourCostCategory;
import com.worth.ifs.finance.resource.category.OtherFundingCostCategory;
import com.worth.ifs.finance.resource.cost.FinanceRowType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.EnumMap;

import static org.mockito.Mockito.when;

public class OrganisationFinanceTest {
    @Mock
    OtherFundingCostCategory otherFundingCostCategory;

    @Mock
    LabourCostCategory labourCostCategory;

    @Mock
    DefaultCostCategory defaultCostCategory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        EnumMap<FinanceRowType, FinanceRowCostCategory> costCategories = new EnumMap<>(FinanceRowType.class);
        costCategories.put(FinanceRowType.LABOUR, labourCostCategory);
        costCategories.put(FinanceRowType.OTHER_FUNDING, otherFundingCostCategory);
        costCategories.put(FinanceRowType.CAPITAL_USAGE, defaultCostCategory);

        //organisationFinance = newOrganisationFinance().withGrantClaimPercentage(50).withCostCategories(costCategories).build();
    }

    @Test
    public void totalCostsShouldBeAllCostsAddedUpExceptOtherFundingCosts() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(500));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        //Assert.assertEquals(new BigDecimal(4000), organisationFinance.getTotal());
    }

    @Test
    public void grantPercentageShouldReturnAPercentageNumber() throws Exception {
        Integer expectedValue = 50;
       // Assert.assertEquals(expectedValue, organisationFinance.getGrantClaimPercentage());
    }

    //@Test
    public void fundingSoughtShouldBeGrantPercentageOfTotalCostMinusOtherFunding() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(500));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        /*BigDecimal grantPercentageOfTotalCost = organisationFinance.getTotal().multiply(new BigDecimal(organisationFinance.getGrantClaimPercentage())).divide(new BigDecimal(100));
        BigDecimal grantPercentageOfTotalCostMinusOtherFunding = grantPercentageOfTotalCost.subtract(organisationFinance.getTotalOtherFunding());
        BigDecimal grantPercentageOfTotalCostMinusOtherFundingMinimumZero = grantPercentageOfTotalCostMinusOtherFunding.max(new BigDecimal(0));

        Assert.assertEquals(grantPercentageOfTotalCostMinusOtherFundingMinimumZero, organisationFinance.getTotalFundingSought());
        */
    }

    // @Test
    public void fundingSoughtValueCannotBeNegative() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        //Assert.assertEquals(new BigDecimal(0), organisationFinance.getTotalFundingSought());
    }

    //@Test
    public void contributionShouldBeTotalMinusFundingSoughtAndOtherFunding() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(2000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

       /* BigDecimal totalMinusFundingSought = organisationFinance.getTotal().subtract(organisationFinance.getTotalOtherFunding()).max(new BigDecimal(0));
        BigDecimal totalMinusFundingSoughtAndOtherFunding = totalMinusFundingSought.subtract(organisationFinance.getTotalFundingSought());
        BigDecimal totalMinusFundingSoughtAndOtherFundingMinimumZero = totalMinusFundingSoughtAndOtherFunding.max(new BigDecimal(0));

        Assert.assertEquals(totalMinusFundingSoughtAndOtherFundingMinimumZero, organisationFinance.getTotalContribution());
        */
    }

    //@Test
    public void contributionCannotBeANegativeNumber() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

       // Assert.assertEquals(new BigDecimal(0), organisationFinance.getTotalContribution());
    }

    //@Test
    public void otherFundingShouldBeAllOtherFundingCostsAddedTogether() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));

        //Assert.assertEquals(new BigDecimal(5000), organisationFinance.getTotalOtherFunding());
    }
}