package com.worth.ifs.application.finance.model;

import com.worth.ifs.application.domain.Question;
import com.worth.ifs.application.finance.*;
import com.worth.ifs.application.finance.cost.OtherFunding;
import com.worth.ifs.application.finance.view.OrganisationFinanceOverview;
import com.worth.ifs.finance.domain.ApplicationFinance;
import com.worth.ifs.finance.domain.Cost;
import com.worth.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.worth.ifs.application.finance.builder.OrganisationFinanceBuilder.newOrganisationFinance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class OrganisationFinanceTest {
    OrganisationFinance organisationFinance;

    @Mock
    OtherFundingCostCategory otherFundingCostCategory;

    @Mock
    LabourCostCategory labourCostCategory;

    @Mock
    DefaultCostCategory defaultCostCategory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        EnumMap<CostType, CostCategory> costCategories = new EnumMap<>(CostType.class);
        costCategories.put(CostType.LABOUR, labourCostCategory);
        costCategories.put(CostType.OTHER_FUNDING, otherFundingCostCategory);
        costCategories.put(CostType.CAPITAL_USAGE, defaultCostCategory);

        organisationFinance = newOrganisationFinance().withGrantClaimPercentage(50).withCostCategories(costCategories).build();
    }

    @Test
    public void totalCostsShouldBeAllCostsAddedUpExceptOtherFundingCosts() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(500));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        Assert.assertEquals(new BigDecimal(4000), organisationFinance.getTotal());
    }

    @Test
    public void grantPercentageShouldReturnAPercentageNumber() throws Exception {
        Integer expectedValue = 50;
        Assert.assertEquals(expectedValue, organisationFinance.getGrantClaimPercentage());
    }

    @Test
    public void fundingSoughtShouldBeGrantPercentageOfTotalCostMinusOtherFunding() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(500));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        BigDecimal grantPercentageOfTotalCost = organisationFinance.getTotal().multiply(new BigDecimal(organisationFinance.getGrantClaimPercentage())).divide(new BigDecimal(100));
        BigDecimal grantPercentageOfTotalCostMinusOtherFunding = grantPercentageOfTotalCost.subtract(organisationFinance.getTotalOtherFunding());
        BigDecimal grantPercentageOfTotalCostMinusOtherFundingMinimumZero = grantPercentageOfTotalCostMinusOtherFunding.max(new BigDecimal(0));

        Assert.assertEquals(grantPercentageOfTotalCostMinusOtherFundingMinimumZero, organisationFinance.getTotalFundingSought());
    }

    @Test
    public void fundingSoughtValueCannotBeNegative() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        Assert.assertEquals(new BigDecimal(0), organisationFinance.getTotalFundingSought());
    }

    @Test
    public void contributionShouldBeTotalMinusFundingSoughtAndOtherFunding() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(2000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        BigDecimal totalMinusFundingSought = organisationFinance.getTotal().subtract(organisationFinance.getTotalOtherFunding()).max(new BigDecimal(0));
        BigDecimal totalMinusFundingSoughtAndOtherFunding = totalMinusFundingSought.subtract(organisationFinance.getTotalFundingSought());
        BigDecimal totalMinusFundingSoughtAndOtherFundingMinimumZero = totalMinusFundingSoughtAndOtherFunding.max(new BigDecimal(0));

        Assert.assertEquals(totalMinusFundingSoughtAndOtherFundingMinimumZero, organisationFinance.getTotalContribution());
    }

    @Test
    public void contributionCannotBeANegativeNumber() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));
        when(otherFundingCostCategory.excludeFromTotalCost()).thenReturn(true);

        Assert.assertEquals(new BigDecimal(0), organisationFinance.getTotalContribution());
    }

    @Test
    public void otherFundingShouldBeAllOtherFundingCostsAddedTogether() throws Exception {
        when(labourCostCategory.getTotal()).thenReturn(new BigDecimal(1000));
        when(defaultCostCategory.getTotal()).thenReturn(new BigDecimal(3000));
        when(otherFundingCostCategory.getTotal()).thenReturn(new BigDecimal(5000));

        Assert.assertEquals(new BigDecimal(5000), organisationFinance.getTotalOtherFunding());
    }
}